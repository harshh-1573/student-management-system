package com.student.management.repository;

import com.student.management.entity.Enrollment;
import com.student.management.util.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @EntityGraph(attributePaths = {"student", "course"})
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    @EntityGraph(attributePaths = {"student", "course", "course.department", "course.teacher"})
    List<Enrollment> findByStudentId(Long studentId);

    @EntityGraph(attributePaths = {"student", "course", "course.department", "course.teacher"})
    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    @EntityGraph(attributePaths = {"student", "course"})
    List<Enrollment> findByCourseId(Long courseId);

    @EntityGraph(attributePaths = {"student", "course"})
    List<Enrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    @EntityGraph(attributePaths = {"student", "course"})
    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

    @EntityGraph(attributePaths = {"student", "course", "course.department", "course.teacher"})
    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    long countByStatus(EnrollmentStatus status);

    @Query("SELECT e.course.name, COUNT(e) FROM Enrollment e WHERE e.status = 'ACTIVE' GROUP BY e.course.name")
    List<Object[]> countEnrollmentsGroupedByCourse();
}
