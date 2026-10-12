package com.student.management.service;

import com.student.management.dto.request.AttendanceBatchRequest;
import com.student.management.dto.response.AttendanceResponse;
import com.student.management.dto.response.AttendanceStatsResponse;
import com.student.management.entity.*;
import com.student.management.exception.UnauthorizedException;
import com.student.management.repository.*;
import com.student.management.service.impl.AttendanceServiceImpl;
import com.student.management.util.AttendanceStatus;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    private AttendanceServiceImpl attendanceService;

    private Course course;
    private Teacher teacher;
    private Student student;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        attendanceService = new AttendanceServiceImpl(
                attendanceRepository,
                enrollmentRepository,
                courseRepository,
                studentRepository,
                userRepository,
                modelMapper
        );

        teacher = Teacher.builder().id(5L).email("teacher@sms.com").name("Prof. Turing").build();
        course = Course.builder().id(20L).code("CS101").teacher(teacher).build();
        student = Student.builder().id(1L).rollNumber("STU-100").name("John Doe").build();
        enrollment = Enrollment.builder().id(30L).course(course).student(student).status(EnrollmentStatus.ACTIVE).build();
    }

    @Test
    @DisplayName("Mark attendance batch should record attendance records for enrolled students")
    void testMarkAttendanceBatch_Success() {
        AttendanceBatchRequest.AttendanceItem item = new AttendanceBatchRequest.AttendanceItem(1L, AttendanceStatus.PRESENT);
        AttendanceBatchRequest request = new AttendanceBatchRequest(20L, LocalDate.now(), List.of(item));

        when(courseRepository.findById(20L)).thenReturn(Optional.of(course));
        when(userRepository.findByEmail("teacher@sms.com")).thenReturn(Optional.of(User.builder().email("teacher@sms.com").role(RoleType.ROLE_TEACHER).build()));
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 20L)).thenReturn(Optional.of(enrollment));
        when(attendanceRepository.findByEnrollmentIdAndDate(30L, request.getDate())).thenReturn(Optional.empty());

        Attendance savedAttendance = Attendance.builder().id(100L).enrollment(enrollment).date(request.getDate()).status(AttendanceStatus.PRESENT).build();
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(savedAttendance);

        List<AttendanceResponse> results = attendanceService.markAttendanceBatch(request, "teacher@sms.com");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Mark attendance should throw UnauthorizedException if teacher is not assigned to course")
    void testMarkAttendanceBatch_TeacherNotAssigned() {
        AttendanceBatchRequest request = new AttendanceBatchRequest(20L, LocalDate.now(), List.of(new AttendanceBatchRequest.AttendanceItem(1L, AttendanceStatus.PRESENT)));

        when(courseRepository.findById(20L)).thenReturn(Optional.of(course));
        when(userRepository.findByEmail("other.teacher@sms.com")).thenReturn(Optional.of(User.builder().email("other.teacher@sms.com").role(RoleType.ROLE_TEACHER).build()));

        assertThrows(UnauthorizedException.class, () -> attendanceService.markAttendanceBatch(request, "other.teacher@sms.com"));
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Get course attendance stats should compute total, present, absent, late and percentage")
    void testGetCourseAttendanceStats_Success() {
        when(courseRepository.findById(20L)).thenReturn(Optional.of(course));
        when(userRepository.findByEmail("teacher@sms.com")).thenReturn(Optional.of(User.builder().email("teacher@sms.com").role(RoleType.ROLE_TEACHER).build()));
        when(enrollmentRepository.findByCourseIdAndStatus(20L, EnrollmentStatus.ACTIVE)).thenReturn(List.of(enrollment));
        when(attendanceRepository.countByEnrollmentId(30L)).thenReturn(10L);
        when(attendanceRepository.countByEnrollmentIdAndStatus(30L, AttendanceStatus.PRESENT)).thenReturn(8L);
        when(attendanceRepository.countByEnrollmentIdAndStatus(30L, AttendanceStatus.ABSENT)).thenReturn(1L);
        when(attendanceRepository.countByEnrollmentIdAndStatus(30L, AttendanceStatus.LATE)).thenReturn(1L);

        List<AttendanceStatsResponse> stats = attendanceService.getCourseAttendanceStats(20L, "teacher@sms.com");

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getAttendancePercentage()).isEqualTo(85.0);
        assertThat(stats.get(0).getTotalClasses()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Get my attendance should return attendance list for authenticated student")
    void testGetMyAttendance_Success() {
        when(studentRepository.findByEmail("john@sms.com")).thenReturn(Optional.of(student));
        Attendance att = Attendance.builder().id(1L).enrollment(enrollment).date(LocalDate.now()).status(AttendanceStatus.PRESENT).build();
        when(attendanceRepository.findAllByStudentId(1L)).thenReturn(List.of(att));

        List<AttendanceResponse> results = attendanceService.getMyAttendance("john@sms.com");

        assertThat(results).hasSize(1);
        verify(attendanceRepository).findAllByStudentId(1L);
    }
}
