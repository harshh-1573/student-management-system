package com.student.management.repository;

import com.student.management.entity.Mark;
import com.student.management.util.ExamType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {

    @EntityGraph(attributePaths = {"enrollment", "enrollment.student", "enrollment.course"})
    List<Mark> findByEnrollmentId(Long enrollmentId);

    @EntityGraph(attributePaths = {"enrollment", "enrollment.student", "enrollment.course"})
    Optional<Mark> findByEnrollmentIdAndExamType(Long enrollmentId, ExamType examType);

    @EntityGraph(attributePaths = {"enrollment", "enrollment.student", "enrollment.course"})
    List<Mark> findByEnrollmentCourseIdAndExamType(Long courseId, ExamType examType);

    @EntityGraph(attributePaths = {"enrollment", "enrollment.student", "enrollment.course"})
    List<Mark> findByEnrollmentCourseId(Long courseId);

    @Query("SELECT m FROM Mark m JOIN FETCH m.enrollment e JOIN FETCH e.student s JOIN FETCH e.course c WHERE s.id = :studentId")
    List<Mark> findAllByStudentId(@Param("studentId") Long studentId);
}
