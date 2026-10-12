package com.student.management.service;

import com.student.management.dto.request.StudentRequest;
import com.student.management.dto.response.StudentReportResponse;
import com.student.management.dto.response.StudentResponse;
import com.student.management.entity.*;
import com.student.management.exception.DuplicateResourceException;
import com.student.management.exception.ResourceNotFoundException;
import com.student.management.repository.*;
import com.student.management.service.impl.StudentServiceImpl;
import com.student.management.util.AttendanceStatus;
import com.student.management.util.EnrollmentStatus;
import com.student.management.util.ExamType;
import com.student.management.util.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private MarkRepository markRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private StudentServiceImpl studentService;

    private Department sampleDepartment;
    private Student sampleStudent;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        studentService = new StudentServiceImpl(
                studentRepository,
                departmentRepository,
                userRepository,
                enrollmentRepository,
                attendanceRepository,
                markRepository,
                passwordEncoder,
                modelMapper
        );

        sampleDepartment = Department.builder()
                .id(1L)
                .name("Computer Science")
                .code("CSE")
                .build();

        sampleUser = User.builder()
                .id(10L)
                .email("student@sms.com")
                .role(RoleType.ROLE_STUDENT)
                .build();

        sampleStudent = Student.builder()
                .id(1L)
                .rollNumber("STU-100")
                .name("John Doe")
                .email("student@sms.com")
                .enrollmentYear(2024)
                .department(sampleDepartment)
                .user(sampleUser)
                .build();
    }

    @Test
    @DisplayName("Create student should succeed when roll number and email are unique")
    void testCreateStudent_Success() {
        StudentRequest request = StudentRequest.builder()
                .rollNumber("STU-100")
                .name("John Doe")
                .email("student@sms.com")
                .enrollmentYear(2024)
                .departmentId(1L)
                .password("Student@123")
                .build();

        when(studentRepository.existsByRollNumber("STU-100")).thenReturn(false);
        when(studentRepository.existsByEmail("student@sms.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(sampleDepartment));
        when(userRepository.findByEmail("student@sms.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        StudentResponse result = studentService.createStudent(request);

        assertThat(result).isNotNull();
        assertThat(result.getRollNumber()).isEqualTo("STU-100");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Create student should throw DuplicateResourceException if roll number exists")
    void testCreateStudent_DuplicateRoll() {
        StudentRequest request = StudentRequest.builder()
                .rollNumber("STU-100")
                .name("John Doe")
                .email("student@sms.com")
                .departmentId(1L)
                .build();

        when(studentRepository.existsByRollNumber("STU-100")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> studentService.createStudent(request));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Get student by ID should throw ResourceNotFoundException when ID is invalid")
    void testGetStudentById_NotFound() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getStudentById(999L, "admin@sms.com"));
    }

    @Test
    @DisplayName("Get student report should calculate course performance, attendance %, marks, and GPA")
    void testGetStudentReport_Success() {
        Course course = Course.builder().id(100L).code("CS101").name("Data Structures").credits(4).build();
        Enrollment enrollment = Enrollment.builder().id(50L).student(sampleStudent).course(course).status(EnrollmentStatus.ACTIVE).build();

        Mark mark1 = Mark.builder().id(1L).enrollment(enrollment).examType(ExamType.MIDTERM).marksObtained(45.0).maxMarks(50.0).build();
        Mark mark2 = Mark.builder().id(2L).enrollment(enrollment).examType(ExamType.FINAL).marksObtained(90.0).maxMarks(100.0).build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));
        when(userRepository.findByEmail("admin@sms.com")).thenReturn(Optional.of(User.builder().role(RoleType.ROLE_ADMIN).build()));
        when(enrollmentRepository.findByStudentIdAndStatus(1L, EnrollmentStatus.ACTIVE)).thenReturn(List.of(enrollment));
        when(attendanceRepository.countByEnrollmentId(50L)).thenReturn(10L);
        when(attendanceRepository.countByEnrollmentIdAndStatus(50L, AttendanceStatus.PRESENT)).thenReturn(9L);
        when(attendanceRepository.countByEnrollmentIdAndStatus(50L, AttendanceStatus.LATE)).thenReturn(1L);
        when(markRepository.findByEnrollmentId(50L)).thenReturn(List.of(mark1, mark2));

        StudentReportResponse report = studentService.getStudentReport(1L, "admin@sms.com");

        assertThat(report).isNotNull();
        assertThat(report.getTotalEnrolledCourses()).isEqualTo(1);
        assertThat(report.getTotalCredits()).isEqualTo(4);
        assertThat(report.getCourses()).hasSize(1);
        assertThat(report.getCourses().get(0).getCourseCode()).isEqualTo("CS101");
        assertThat(report.getCourses().get(0).getAttendancePercentage()).isEqualTo(95.0);
        assertThat(report.getCourses().get(0).getGrade()).isEqualTo("A+");
        assertThat(report.getCourses().get(0).getGradePoint()).isEqualTo(10.0);
        assertThat(report.getCumulativeGpa()).isEqualTo(10.0);
    }
}
