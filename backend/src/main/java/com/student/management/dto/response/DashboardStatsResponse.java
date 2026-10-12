package com.student.management.dto.response;

import java.util.HashMap;
import java.util.Map;

public class DashboardStatsResponse {

    private long totalStudents;
    private long totalTeachers;
    private long totalCourses;
    private long totalDepartments;
    private long totalEnrollments;
    private Map<String, Long> studentsPerDepartment = new HashMap<>();
    private Map<String, Long> enrollmentsPerCourse = new HashMap<>();

    public DashboardStatsResponse() {}

    public DashboardStatsResponse(long totalStudents, long totalTeachers, long totalCourses, long totalDepartments, long totalEnrollments, Map<String, Long> studentsPerDepartment, Map<String, Long> enrollmentsPerCourse) {
        this.totalStudents = totalStudents;
        this.totalTeachers = totalTeachers;
        this.totalCourses = totalCourses;
        this.totalDepartments = totalDepartments;
        this.totalEnrollments = totalEnrollments;
        this.studentsPerDepartment = studentsPerDepartment != null ? studentsPerDepartment : new HashMap<>();
        this.enrollmentsPerCourse = enrollmentsPerCourse != null ? enrollmentsPerCourse : new HashMap<>();
    }

    public static DashboardStatsResponseBuilder builder() {
        return new DashboardStatsResponseBuilder();
    }

    public static class DashboardStatsResponseBuilder {
        private long totalStudents;
        private long totalTeachers;
        private long totalCourses;
        private long totalDepartments;
        private long totalEnrollments;
        private Map<String, Long> studentsPerDepartment = new HashMap<>();
        private Map<String, Long> enrollmentsPerCourse = new HashMap<>();

        public DashboardStatsResponseBuilder totalStudents(long totalStudents) { this.totalStudents = totalStudents; return this; }
        public DashboardStatsResponseBuilder totalTeachers(long totalTeachers) { this.totalTeachers = totalTeachers; return this; }
        public DashboardStatsResponseBuilder totalCourses(long totalCourses) { this.totalCourses = totalCourses; return this; }
        public DashboardStatsResponseBuilder totalDepartments(long totalDepartments) { this.totalDepartments = totalDepartments; return this; }
        public DashboardStatsResponseBuilder totalEnrollments(long totalEnrollments) { this.totalEnrollments = totalEnrollments; return this; }
        public DashboardStatsResponseBuilder studentsPerDepartment(Map<String, Long> studentsPerDepartment) { this.studentsPerDepartment = studentsPerDepartment; return this; }
        public DashboardStatsResponseBuilder enrollmentsPerCourse(Map<String, Long> enrollmentsPerCourse) { this.enrollmentsPerCourse = enrollmentsPerCourse; return this; }

        public DashboardStatsResponse build() {
            return new DashboardStatsResponse(totalStudents, totalTeachers, totalCourses, totalDepartments, totalEnrollments, studentsPerDepartment, enrollmentsPerCourse);
        }
    }

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalTeachers() { return totalTeachers; }
    public void setTotalTeachers(long totalTeachers) { this.totalTeachers = totalTeachers; }

    public long getTotalCourses() { return totalCourses; }
    public void setTotalCourses(long totalCourses) { this.totalCourses = totalCourses; }

    public long getTotalDepartments() { return totalDepartments; }
    public void setTotalDepartments(long totalDepartments) { this.totalDepartments = totalDepartments; }

    public long getTotalEnrollments() { return totalEnrollments; }
    public void setTotalEnrollments(long totalEnrollments) { this.totalEnrollments = totalEnrollments; }

    public Map<String, Long> getStudentsPerDepartment() { return studentsPerDepartment; }
    public void setStudentsPerDepartment(Map<String, Long> studentsPerDepartment) { this.studentsPerDepartment = studentsPerDepartment; }

    public Map<String, Long> getEnrollmentsPerCourse() { return enrollmentsPerCourse; }
    public void setEnrollmentsPerCourse(Map<String, Long> enrollmentsPerCourse) { this.enrollmentsPerCourse = enrollmentsPerCourse; }
}
