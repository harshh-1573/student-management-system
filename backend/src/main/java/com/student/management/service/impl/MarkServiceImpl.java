package com.student.management.service.impl;

import com.student.management.dto.request.MarkRequest;
import com.student.management.dto.response.MarkResponse;
import com.student.management.entity.*;
import com.student.management.exception.BadRequestException;
import com.student.management.exception.ResourceNotFoundException;
import com.student.management.exception.UnauthorizedException;
import com.student.management.repository.*;
import com.student.management.service.MarkService;
import com.student.management.util.EnrollmentStatus;
import com.student.management.util.ExamType;
import com.student.management.util.RoleType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarkServiceImpl implements MarkService {

    private static final Logger log = LoggerFactory.getLogger(MarkServiceImpl.class);

    private final MarkRepository markRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public MarkServiceImpl(MarkRepository markRepository,
                           EnrollmentRepository enrollmentRepository,
                           CourseRepository courseRepository,
                           StudentRepository studentRepository,
                           UserRepository userRepository,
                           ModelMapper modelMapper) {
        this.markRepository = markRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private MarkResponse mapToResponse(Mark mark) {
        MarkResponse res = modelMapper.map(mark, MarkResponse.class);
        if (mark.getEnrollment() != null) {
            res.setEnrollmentId(mark.getEnrollment().getId());
            if (mark.getEnrollment().getStudent() != null) {
                res.setStudentId(mark.getEnrollment().getStudent().getId());
                res.setStudentName(mark.getEnrollment().getStudent().getName());
                res.setStudentRollNumber(mark.getEnrollment().getStudent().getRollNumber());
            }
            if (mark.getEnrollment().getCourse() != null) {
                res.setCourseId(mark.getEnrollment().getCourse().getId());
                res.setCourseCode(mark.getEnrollment().getCourse().getCode());
                res.setCourseName(mark.getEnrollment().getCourse().getName());
            }
        }
        double percentage = mark.getMaxMarks() > 0 ?
                Math.round((mark.getMarksObtained() / mark.getMaxMarks()) * 1000.0) / 10.0 : 0.0;
        res.setPercentage(percentage);
        return res;
    }

    private void verifyCourseTeacherOwnership(Course course, String callerEmail) {
        if (callerEmail != null) {
            User caller = userRepository.findByEmail(callerEmail).orElse(null);
            if (caller != null && caller.getRole() == RoleType.ROLE_TEACHER) {
                if (course.getTeacher() == null || !course.getTeacher().getEmail().equalsIgnoreCase(callerEmail)) {
                    throw new UnauthorizedException("Access Denied: You can only manage marks for your assigned courses");
                }
            }
        }
    }

    @Override
    @Transactional
    public MarkResponse saveMark(MarkRequest request, String teacherEmail) {
        log.info("Saving mark for enrollment ID: {}, exam type: {}", request.getEnrollmentId(), request.getExamType());
        if (request.getMarksObtained() > request.getMaxMarks()) {
            throw new BadRequestException("Marks obtained cannot be greater than maximum marks");
        }

        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", request.getEnrollmentId()));

        verifyCourseTeacherOwnership(enrollment.getCourse(), teacherEmail);

        Optional<Mark> existingOpt = markRepository.findByEnrollmentIdAndExamType(enrollment.getId(), request.getExamType());
        Mark mark;
        if (existingOpt.isPresent()) {
            mark = existingOpt.get();
            mark.setMarksObtained(request.getMarksObtained());
            mark.setMaxMarks(request.getMaxMarks());
        } else {
            mark = Mark.builder()
                    .enrollment(enrollment)
                    .examType(request.getExamType())
                    .marksObtained(request.getMarksObtained())
                    .maxMarks(request.getMaxMarks())
                    .build();
        }

        Mark saved = markRepository.save(mark);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public List<MarkResponse> saveMarkBatch(MarkRequest.MarkBatchRequest request, String teacherEmail) {
        log.info("Saving marks in batch for course ID: {}, exam type: {}", request.getCourseId(), request.getExamType());
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        verifyCourseTeacherOwnership(course, teacherEmail);

        List<MarkResponse> responses = new ArrayList<>();
        for (MarkRequest.StudentMarkItem item : request.getItems()) {
            if (item.getMarksObtained() > request.getMaxMarks()) {
                throw new BadRequestException("Marks obtained cannot be greater than maximum marks for student ID: " + item.getStudentId());
            }

            Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(item.getStudentId(), course.getId())
                    .orElseThrow(() -> new BadRequestException("Student ID " + item.getStudentId() + " is not enrolled in course " + course.getCode()));

            if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
                continue;
            }

            Optional<Mark> existingOpt = markRepository.findByEnrollmentIdAndExamType(enrollment.getId(), request.getExamType());
            Mark mark;
            if (existingOpt.isPresent()) {
                mark = existingOpt.get();
                mark.setMarksObtained(item.getMarksObtained());
                mark.setMaxMarks(request.getMaxMarks());
            } else {
                mark = Mark.builder()
                        .enrollment(enrollment)
                        .examType(request.getExamType())
                        .marksObtained(item.getMarksObtained())
                        .maxMarks(request.getMaxMarks())
                        .build();
            }

            Mark saved = markRepository.save(mark);
            responses.add(mapToResponse(saved));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponse> getMarksByCourse(Long courseId, ExamType examType, String teacherEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        verifyCourseTeacherOwnership(course, teacherEmail);

        List<Mark> marks;
        if (examType != null) {
            marks = markRepository.findByEnrollmentCourseIdAndExamType(courseId, examType);
        } else {
            marks = markRepository.findByEnrollmentCourseId(courseId);
        }

        return marks.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponse> getMarksByEnrollment(Long enrollmentId, String currentUserEmail) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        if (currentUserEmail != null) {
            User caller = userRepository.findByEmail(currentUserEmail).orElse(null);
            if (caller != null && caller.getRole() == RoleType.ROLE_STUDENT) {
                if (enrollment.getStudent().getUser() == null ||
                        !enrollment.getStudent().getUser().getEmail().equalsIgnoreCase(currentUserEmail)) {
                    throw new UnauthorizedException("Access Denied: You can only view your own marks");
                }
            }
        }

        return markRepository.findByEnrollmentId(enrollmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponse> getMyMarks(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "email", studentEmail));

        return markRepository.findAllByStudentId(student.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
