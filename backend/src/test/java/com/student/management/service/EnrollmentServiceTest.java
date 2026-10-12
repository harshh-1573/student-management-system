package com.student.management.service;

import com.student.management.dto.request.EnrollmentRequest;
import com.student.management.dto.response.EnrollmentResponse;
import com.student.management.entity.Course;
import com.student.management.entity.Enrollment;
import com.student.management.entity.Student;
import com.student.management.entity.User;
import com.student.management.exception.DuplicateResourceException;
import com.student.management.exception.UnauthorizedException;
import com.student.management.repository.CourseRepository;
import com.student.management.repository.EnrollmentRepository;
import com.student.management.repository.StudentRepository;
import com.student.management.repository.UserRepository;
import com.student.management.service.impl.EnrollmentServiceImpl;
import com.student.management.util.EnrollmentStatus;
import com.student.management.util.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    private EnrollmentServiceImpl enrollmentService;

    private Student student;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        enrollmentService = new EnrollmentServiceImpl(
                enrollmentRepository,
                studentRepository,
                courseRepository,
                userRepository,
                modelMapper
        );

        student = Student.builder().id(1L).name("John Doe").rollNumber("STU-100").email("john@sms.com").build();
        course = Course.builder().id(2L).code("CS101").name("DSA").credits(4).build();
        enrollment = Enrollment.builder()
                .id(10L)
                .student(student)
                .course(course)
                .enrollmentDate(LocalDate.now())
                .status(EnrollmentStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Enroll student should create active enrollment when not already enrolled")
    void testEnrollStudent_Success() {
        EnrollmentRequest request = new EnrollmentRequest(1L, 2L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 2L)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        EnrollmentResponse result = enrollmentService.enrollStudent(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Enroll student should throw DuplicateResourceException if already actively enrolled")
    void testEnrollStudent_AlreadyEnrolled() {
        EnrollmentRequest request = new EnrollmentRequest(1L, 2L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 2L)).thenReturn(Optional.of(enrollment));

        assertThrows(DuplicateResourceException.class, () -> enrollmentService.enrollStudent(request));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Drop student should set status to DROPPED")
    void testDropStudent_Success() {
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(enrollment));
        when(userRepository.findByEmail("admin@sms.com")).thenReturn(Optional.of(User.builder().role(RoleType.ROLE_ADMIN).build()));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        EnrollmentResponse result = enrollmentService.dropStudent(10L, "admin@sms.com");

        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    @DisplayName("Drop student should throw UnauthorizedException if student attempts to drop another student's course")
    void testDropStudent_Unauthorized() {
        User otherUser = User.builder().email("john@sms.com").role(RoleType.ROLE_STUDENT).build();
        student.setUser(otherUser);

        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(enrollment));
        when(userRepository.findByEmail("intruder@sms.com")).thenReturn(Optional.of(User.builder().email("intruder@sms.com").role(RoleType.ROLE_STUDENT).build()));

        assertThrows(UnauthorizedException.class, () -> enrollmentService.dropStudent(10L, "intruder@sms.com"));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }
}
