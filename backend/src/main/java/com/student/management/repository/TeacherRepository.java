package com.student.management.repository;

import com.student.management.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @EntityGraph(attributePaths = {"department", "user"})
    Optional<Teacher> findByEmail(String email);

    @EntityGraph(attributePaths = {"department", "user"})
    Optional<Teacher> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"department"})
    List<Teacher> findByDepartmentId(Long departmentId);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"department", "user"})
    Page<Teacher> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);
}
