package com.student.management.repository;

import com.student.management.entity.Attendance;
import com.student.management.util.AttendanceStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @EntityGraph(attributePaths = {"enrollment", "enrollment.student", "enrollment.course"})
    List<Attendance> findByEnrollmentId(Long enrollmentId);

    @EntityGraph(attributePaths = {"enrollment", "enrollment.student", "enrollment.course"})
    Optional<Attendance> findByEnrollmentIdAndDate(Long enrollmentId, LocalDate date);

    @EntityGraph(attributePaths = {"enrollment", "enrollment.student", "enrollment.course"})
    List<Attendance> findByEnrollmentCourseIdAndDate(Long courseId, LocalDate date);

    @EntityGraph(attributePaths = {"enrollment", "enrollment.student", "enrollment.course"})
    List<Attendance> findByEnrollmentCourseId(Long courseId);

    long countByEnrollmentIdAndStatus(Long enrollmentId, AttendanceStatus status);

    long countByEnrollmentId(Long enrollmentId);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.enrollment e JOIN FETCH e.student s JOIN FETCH e.course c WHERE s.id = :studentId")
    List<Attendance> findAllByStudentId(@Param("studentId") Long studentId);
}
