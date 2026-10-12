package com.student.management.service.impl;

import com.student.management.dto.request.AttendanceBatchRequest;
import com.student.management.dto.response.AttendanceResponse;
import com.student.management.dto.response.AttendanceStatsResponse;
import com.student.management.entity.*;
import com.student.management.exception.BadRequestException;
import com.student.management.exception.ResourceNotFoundException;
import com.student.management.exception.UnauthorizedException;
import com.student.management.repository.*;
import com.student.management.service.AttendanceService;
import com.student.management.util.AttendanceStatus;
import com.student.management.util.EnrollmentStatus;
import com.student.management.util.RoleType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 EnrollmentRepository enrollmentRepository,
                                 CourseRepository courseRepository,
                                 StudentRepository studentRepository,
                                 UserRepository userRepository,
                                 ModelMapper modelMapper) {
        this.attendanceRepository = attendanceRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        AttendanceResponse res = modelMapper.map(attendance, AttendanceResponse.class);
        if (attendance.getEnrollment() != null) {
            res.setEnrollmentId(attendance.getEnrollment().getId());
            if (attendance.getEnrollment().getStudent() != null) {
                res.setStudentId(attendance.getEnrollment().getStudent().getId());
                res.setStudentName(attendance.getEnrollment().getStudent().getName());
                res.setStudentRollNumber(attendance.getEnrollment().getStudent().getRollNumber());
            }
            if (attendance.getEnrollment().getCourse() != null) {
                res.setCourseId(attendance.getEnrollment().getCourse().getId());
                res.setCourseCode(attendance.getEnrollment().getCourse().getCode());
                res.setCourseName(attendance.getEnrollment().getCourse().getName());
            }
        }
        return res;
    }

    private void verifyCourseTeacherOwnership(Course course, String callerEmail) {
        if (callerEmail != null) {
            User caller = userRepository.findByEmail(callerEmail).orElse(null);
            if (caller != null && caller.getRole() == RoleType.ROLE_TEACHER) {
                if (course.getTeacher() == null || !course.getTeacher().getEmail().equalsIgnoreCase(callerEmail)) {
                    throw new UnauthorizedException("Access Denied: You can only manage attendance for your assigned courses");
                }
            }
        }
    }

    @Override
    @Transactional
    public List<AttendanceResponse> markAttendanceBatch(AttendanceBatchRequest request, String teacherEmail) {
        log.info("Marking attendance batch for course ID: {} on date: {}", request.getCourseId(), request.getDate());
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        verifyCourseTeacherOwnership(course, teacherEmail);

        List<AttendanceResponse> responseList = new ArrayList<>();

        for (AttendanceBatchRequest.AttendanceItem item : request.getRecords()) {
            Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(item.getStudentId(), course.getId())
                    .orElseThrow(() -> new BadRequestException("Student ID " + item.getStudentId() + " is not enrolled in course " + course.getCode()));

            if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
                throw new BadRequestException("Student ID " + item.getStudentId() + " enrollment is not ACTIVE");
            }

            Optional<Attendance> existingOpt = attendanceRepository.findByEnrollmentIdAndDate(enrollment.getId(), request.getDate());
            Attendance attendance;
            if (existingOpt.isPresent()) {
                attendance = existingOpt.get();
                attendance.setStatus(item.getStatus());
            } else {
                attendance = Attendance.builder()
                        .enrollment(enrollment)
                        .date(request.getDate())
                        .status(item.getStatus())
                        .build();
            }

            Attendance saved = attendanceRepository.save(attendance);
            responseList.add(mapToResponse(saved));
        }

        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByCourseAndDate(Long courseId, LocalDate date, String teacherEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        verifyCourseTeacherOwnership(course, teacherEmail);

        return attendanceRepository.findByEnrollmentCourseIdAndDate(courseId, date).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceStatsResponse> getCourseAttendanceStats(Long courseId, String teacherEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        verifyCourseTeacherOwnership(course, teacherEmail);

        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdAndStatus(courseId, EnrollmentStatus.ACTIVE);
        List<AttendanceStatsResponse> statsList = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            long totalClasses = attendanceRepository.countByEnrollmentId(enrollment.getId());
            long presentCount = attendanceRepository.countByEnrollmentIdAndStatus(enrollment.getId(), AttendanceStatus.PRESENT);
            long absentCount = attendanceRepository.countByEnrollmentIdAndStatus(enrollment.getId(), AttendanceStatus.ABSENT);
            long lateCount = attendanceRepository.countByEnrollmentIdAndStatus(enrollment.getId(), AttendanceStatus.LATE);

            double percentage = totalClasses > 0 ?
                    Math.round(((presentCount + (lateCount * 0.5)) / totalClasses) * 1000.0) / 10.0 : 100.0;

            statsList.add(AttendanceStatsResponse.builder()
                    .studentId(enrollment.getStudent().getId())
                    .studentName(enrollment.getStudent().getName())
                    .studentRollNumber(enrollment.getStudent().getRollNumber())
                    .courseId(course.getId())
                    .courseCode(course.getCode())
                    .courseName(course.getName())
                    .totalClasses(totalClasses)
                    .presentClasses(presentCount)
                    .absentClasses(absentCount)
                    .lateClasses(lateCount)
                    .attendancePercentage(percentage)
                    .build());
        }

        return statsList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendance(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "email", studentEmail));

        return attendanceRepository.findAllByStudentId(student.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
