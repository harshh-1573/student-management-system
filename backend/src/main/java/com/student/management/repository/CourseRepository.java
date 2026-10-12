package com.student.management.repository;

import com.student.management.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @EntityGraph(attributePaths = {"department", "teacher"})
    Optional<Course> findByCode(String code);

    boolean existsByCode(String code);

    @EntityGraph(attributePaths = {"department", "teacher"})
    List<Course> findByTeacherId(Long teacherId);

    @EntityGraph(attributePaths = {"department", "teacher"})
    List<Course> findByDepartmentId(Long departmentId);

    @EntityGraph(attributePaths = {"department", "teacher"})
    Page<Course> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
            String name, String code, Pageable pageable);
}
