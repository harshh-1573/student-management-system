package com.student.management.service.impl;

import com.student.management.dto.response.DashboardStatsResponse;
import com.student.management.repository.*;
import com.student.management.service.DashboardService;
import com.student.management.util.EnrollmentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public DashboardServiceImpl(StudentRepository studentRepository,
                                TeacherRepository teacherRepository,
                                CourseRepository courseRepository,
                                DepartmentRepository departmentRepository,
                                EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalCourses = courseRepository.count();
        long totalDepartments = departmentRepository.count();
        long totalEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);

        Map<String, Long> studentsPerDept = new HashMap<>();
        List<Object[]> deptResults = studentRepository.countStudentsGroupedByDepartment();
        for (Object[] row : deptResults) {
            String deptName = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            studentsPerDept.put(deptName, count);
        }

        Map<String, Long> enrollmentsPerCourse = new HashMap<>();
        List<Object[]> courseResults = enrollmentRepository.countEnrollmentsGroupedByCourse();
        for (Object[] row : courseResults) {
            String courseName = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            enrollmentsPerCourse.put(courseName, count);
        }

        return DashboardStatsResponse.builder()
                .totalStudents(totalStudents)
                .totalTeachers(totalTeachers)
                .totalCourses(totalCourses)
                .totalDepartments(totalDepartments)
                .totalEnrollments(totalEnrollments)
                .studentsPerDepartment(studentsPerDept)
                .enrollmentsPerCourse(enrollmentsPerCourse)
                .build();
    }
}
