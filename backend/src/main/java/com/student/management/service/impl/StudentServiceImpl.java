package com.student.management.service.impl;

import com.student.management.dto.request.StudentRequest;
import com.student.management.dto.response.*;
import com.student.management.entity.*;
import com.student.management.exception.DuplicateResourceException;
import com.student.management.exception.ResourceNotFoundException;
import com.student.management.exception.UnauthorizedException;
import com.student.management.repository.*;
import com.student.management.service.StudentService;
import com.student.management.util.AttendanceStatus;
import com.student.management.util.EnrollmentStatus;
import com.student.management.util.Grade;
import com.student.management.util.RoleType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public StudentServiceImpl(StudentRepository studentRepository,
                              DepartmentRepository departmentRepository,
                              UserRepository userRepository,
                              EnrollmentRepository enrollmentRepository,
                              AttendanceRepository attendanceRepository,
                              MarkRepository markRepository,
                              PasswordEncoder passwordEncoder,
                              ModelMapper modelMapper) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    private StudentResponse mapToResponse(Student student) {
        StudentResponse res = modelMapper.map(student, StudentResponse.class);
        if (student.getDepartment() != null) {
            res.setDepartmentId(student.getDepartment().getId());
            res.setDepartmentName(student.getDepartment().getName());
            res.setDepartmentCode(student.getDepartment().getCode());
        }
        if (student.getUser() != null) {
            res.setUserId(student.getUser().getId());
        }
        return res;
    }

    private void checkStudentAccessOwnership(Student student, String currentUserEmail) {
        if (currentUserEmail != null) {
            User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
            if (currentUser != null && currentUser.getRole() == RoleType.ROLE_STUDENT) {
                if (student.getUser() == null || !student.getUser().getEmail().equalsIgnoreCase(currentUserEmail)) {
                    throw new UnauthorizedException("Access Denied: You can only view your own student profile and records");
                }
            }
        }
    }

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        log.info("Creating student with roll number: {}", request.getRollNumber());
        if (studentRepository.existsByRollNumber(request.getRollNumber())) {
            throw new DuplicateResourceException("Student with roll number already exists: " + request.getRollNumber());
        }
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Student with email already exists: " + request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            String rawPassword = StringUtils.hasText(request.getPassword()) ? request.getPassword() : "Student@123";
            user = User.builder()
                    .fullName(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(rawPassword))
                    .role(RoleType.ROLE_STUDENT)
                    .build();
            user = userRepository.save(user);
        }

        Student student = Student.builder()
                .rollNumber(request.getRollNumber())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .enrollmentYear(request.getEnrollmentYear())
                .department(department)
                .user(user)
                .build();

        Student saved = studentRepository.save(student);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request, String currentUserEmail) {
        log.info("Updating student with ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        checkStudentAccessOwnership(student, currentUserEmail);

        if (!student.getRollNumber().equalsIgnoreCase(request.getRollNumber()) &&
                studentRepository.existsByRollNumber(request.getRollNumber())) {
            throw new DuplicateResourceException("Student with roll number already exists: " + request.getRollNumber());
        }
        if (!student.getEmail().equalsIgnoreCase(request.getEmail()) &&
                studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Student with email already exists: " + request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        student.setRollNumber(request.getRollNumber());
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());
        student.setEnrollmentYear(request.getEnrollmentYear());
        student.setDepartment(department);

        if (student.getUser() != null) {
            student.getUser().setFullName(request.getName());
            student.getUser().setEmail(request.getEmail());
            if (StringUtils.hasText(request.getPassword())) {
                student.getUser().setPassword(passwordEncoder.encode(request.getPassword()));
            }
        }

        Student updated = studentRepository.save(student);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        log.info("Deleting student with ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        studentRepository.delete(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id, String currentUserEmail) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        checkStudentAccessOwnership(student, currentUserEmail);
        return mapToResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "email", email));
        return mapToResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getAllStudents(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Student> studentPage;
        if (StringUtils.hasText(search)) {
            studentPage = studentRepository.findByNameContainingIgnoreCaseOrRollNumberContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, search, pageable);
        } else {
            studentPage = studentRepository.findAll(pageable);
        }

        List<StudentResponse> content = studentPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.of(studentPage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudentsList() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentReportResponse getStudentReport(Long studentId, String currentUserEmail) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        checkStudentAccessOwnership(student, currentUserEmail);

        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.ACTIVE);

        List<CoursePerformanceResponse> performanceList = new ArrayList<>();
        double totalWeightedGpa = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();

            // Calculate attendance percentage for this course
            long totalClasses = attendanceRepository.countByEnrollmentId(enrollment.getId());
            long presentCount = attendanceRepository.countByEnrollmentIdAndStatus(enrollment.getId(), AttendanceStatus.PRESENT);
            long lateCount = attendanceRepository.countByEnrollmentIdAndStatus(enrollment.getId(), AttendanceStatus.LATE);
            // Count late as 0.5 attendance
            double attendancePercentage = totalClasses > 0 ?
                    Math.round(((presentCount + (lateCount * 0.5)) / totalClasses) * 1000.0) / 10.0 : 100.0;

            // Calculate marks for this course
            List<Mark> marks = markRepository.findByEnrollmentId(enrollment.getId());
            List<MarkResponse> markResponses = marks.stream().map(m -> {
                double pct = m.getMaxMarks() > 0 ? Math.round((m.getMarksObtained() / m.getMaxMarks()) * 1000.0) / 10.0 : 0.0;
                return MarkResponse.builder()
                        .id(m.getId())
                        .enrollmentId(enrollment.getId())
                        .studentId(student.getId())
                        .studentName(student.getName())
                        .studentRollNumber(student.getRollNumber())
                        .courseId(course.getId())
                        .courseCode(course.getCode())
                        .courseName(course.getName())
                        .examType(m.getExamType())
                        .marksObtained(m.getMarksObtained())
                        .maxMarks(m.getMaxMarks())
                        .percentage(pct)
                        .build();
            }).collect(Collectors.toList());

            double totalObtained = marks.stream().mapToDouble(Mark::getMarksObtained).sum();
            double totalMax = marks.stream().mapToDouble(Mark::getMaxMarks).sum();
            double overallPercentage = totalMax > 0 ? Math.round((totalObtained / totalMax) * 1000.0) / 10.0 : 0.0;

            Grade grade = Grade.fromPercentage(overallPercentage);
            double gradePoint = grade.getGradePoint();

            totalWeightedGpa += (gradePoint * course.getCredits());
            totalCredits += course.getCredits();

            performanceList.add(CoursePerformanceResponse.builder()
                    .courseId(course.getId())
                    .courseCode(course.getCode())
                    .courseName(course.getName())
                    .credits(course.getCredits())
                    .semester(course.getSemester())
                    .attendancePercentage(attendancePercentage)
                    .marks(markResponses)
                    .totalMarksObtained(totalObtained)
                    .totalMaxMarks(totalMax)
                    .overallPercentage(overallPercentage)
                    .grade(grade.getLabel())
                    .gradePoint(gradePoint)
                    .build());
        }

        double cumulativeGpa = totalCredits > 0 ?
                Math.round((totalWeightedGpa / totalCredits) * 100.0) / 100.0 : 0.0;

        return StudentReportResponse.builder()
                .student(mapToResponse(student))
                .courses(performanceList)
                .totalEnrolledCourses(enrollments.size())
                .totalCredits(totalCredits)
                .cumulativeGpa(cumulativeGpa)
                .build();
    }
}
