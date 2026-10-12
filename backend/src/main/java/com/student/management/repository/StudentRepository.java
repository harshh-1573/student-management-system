package com.student.management.repository;

import com.student.management.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @EntityGraph(attributePaths = {"department", "user"})
    Optional<Student> findByRollNumber(String rollNumber);

    @EntityGraph(attributePaths = {"department", "user"})
    Optional<Student> findByEmail(String email);

    @EntityGraph(attributePaths = {"department", "user"})
    Optional<Student> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"department"})
    List<Student> findByDepartmentId(Long departmentId);

    boolean existsByRollNumber(String rollNumber);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"department", "user"})
    Page<Student> findByNameContainingIgnoreCaseOrRollNumberContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String rollNumber, String email, Pageable pageable);

    @Query("SELECT s.department.name, COUNT(s) FROM Student s GROUP BY s.department.name")
    List<Object[]> countStudentsGroupedByDepartment();
}
