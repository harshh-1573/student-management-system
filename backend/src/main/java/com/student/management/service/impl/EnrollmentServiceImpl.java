package com.student.management.service.impl;

import com.student.management.dto.request.EnrollmentRequest;
import com.student.management.dto.response.EnrollmentResponse;
import com.student.management.dto.response.PageResponse;
import com.student.management.entity.Course;
import com.student.management.entity.Enrollment;
import com.student.management.entity.Student;
import com.student.management.entity.User;
import com.student.management.exception.DuplicateResourceException;
import com.student.management.exception.ResourceNotFoundException;
import com.student.management.exception.UnauthorizedException;
import com.student.management.repository.CourseRepository;
import com.student.management.repository.EnrollmentRepository;
import com.student.management.repository.StudentRepository;
import com.student.management.repository.UserRepository;
import com.student.management.service.EnrollmentService;
import com.student.management.util.EnrollmentStatus;
import com.student.management.util.RoleType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository,
                                 UserRepository userRepository,
                                 ModelMapper modelMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        EnrollmentResponse res = modelMapper.map(enrollment, EnrollmentResponse.class);
        if (enrollment.getStudent() != null) {
            res.setStudentId(enrollment.getStudent().getId());
            res.setStudentName(enrollment.getStudent().getName());
            res.setStudentRollNumber(enrollment.getStudent().getRollNumber());
            res.setStudentEmail(enrollment.getStudent().getEmail());
        }
        if (enrollment.getCourse() != null) {
            res.setCourseId(enrollment.getCourse().getId());
            res.setCourseCode(enrollment.getCourse().getCode());
            res.setCourseName(enrollment.getCourse().getName());
            res.setCredits(enrollment.getCourse().getCredits());
            res.setSemester(enrollment.getCourse().getSemester());
        }
        return res;
    }

    @Override
    @Transactional
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        log.info("Enrolling student ID {} into course ID {}", request.getStudentId(), request.getCourseId());
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        Optional<Enrollment> existingOpt = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
        if (existingOpt.isPresent()) {
            Enrollment existing = existingOpt.get();
            if (existing.getStatus() == EnrollmentStatus.ACTIVE) {
                throw new DuplicateResourceException("Student is already actively enrolled in course: " + course.getCode());
            } else {
                // Reactivate dropped enrollment
                existing.setStatus(EnrollmentStatus.ACTIVE);
                existing.setEnrollmentDate(LocalDate.now());
                Enrollment reactivated = enrollmentRepository.save(existing);
                return mapToResponse(reactivated);
            }
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollmentDate(LocalDate.now())
                .status(EnrollmentStatus.ACTIVE)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EnrollmentResponse dropStudent(Long enrollmentId, String currentUserEmail) {
        log.info("Dropping enrollment ID: {}", enrollmentId);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        if (currentUserEmail != null) {
            User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
            if (currentUser != null && currentUser.getRole() == RoleType.ROLE_STUDENT) {
                if (enrollment.getStudent().getUser() == null ||
                        !enrollment.getStudent().getUser().getEmail().equalsIgnoreCase(currentUserEmail)) {
                    throw new UnauthorizedException("Access Denied: You can only drop your own course enrollments");
                }
            }
        }

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        Enrollment updated = enrollmentRepository.save(enrollment);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EnrollmentResponse> getEnrollmentsByCourse(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Enrollment> enrollmentPage = enrollmentRepository.findByCourseId(courseId, pageable);

        List<EnrollmentResponse> content = enrollmentPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.of(enrollmentPage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EnrollmentResponse> getEnrollmentsByStudent(Long studentId, int page, int size, String currentUserEmail) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        if (currentUserEmail != null) {
            User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
            if (currentUser != null && currentUser.getRole() == RoleType.ROLE_STUDENT) {
                if (student.getUser() == null || !student.getUser().getEmail().equalsIgnoreCase(currentUserEmail)) {
                    throw new UnauthorizedException("Access Denied: You can only view your own enrollments");
                }
            }
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Enrollment> enrollmentPage = enrollmentRepository.findByStudentId(studentId, pageable);

        List<EnrollmentResponse> content = enrollmentPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.of(enrollmentPage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "email", studentEmail));

        return enrollmentRepository.findByStudentIdAndStatus(student.getId(), EnrollmentStatus.ACTIVE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
