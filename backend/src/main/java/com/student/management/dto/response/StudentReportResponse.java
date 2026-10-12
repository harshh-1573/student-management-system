package com.student.management.dto.response;

import java.util.ArrayList;
import java.util.List;

public class StudentReportResponse {

    private StudentResponse student;
    private List<CoursePerformanceResponse> courses = new ArrayList<>();
    private int totalEnrolledCourses;
    private int totalCredits;
    private double cumulativeGpa;

    public StudentReportResponse() {}

    public StudentReportResponse(StudentResponse student, List<CoursePerformanceResponse> courses, int totalEnrolledCourses, int totalCredits, double cumulativeGpa) {
        this.student = student;
        this.courses = courses != null ? courses : new ArrayList<>();
        this.totalEnrolledCourses = totalEnrolledCourses;
        this.totalCredits = totalCredits;
        this.cumulativeGpa = cumulativeGpa;
    }

    public static StudentReportResponseBuilder builder() {
        return new StudentReportResponseBuilder();
    }

    public static class StudentReportResponseBuilder {
        private StudentResponse student;
        private List<CoursePerformanceResponse> courses = new ArrayList<>();
        private int totalEnrolledCourses;
        private int totalCredits;
        private double cumulativeGpa;

        public StudentReportResponseBuilder student(StudentResponse student) { this.student = student; return this; }
        public StudentReportResponseBuilder courses(List<CoursePerformanceResponse> courses) { this.courses = courses; return this; }
        public StudentReportResponseBuilder totalEnrolledCourses(int totalEnrolledCourses) { this.totalEnrolledCourses = totalEnrolledCourses; return this; }
        public StudentReportResponseBuilder totalCredits(int totalCredits) { this.totalCredits = totalCredits; return this; }
        public StudentReportResponseBuilder cumulativeGpa(double cumulativeGpa) { this.cumulativeGpa = cumulativeGpa; return this; }

        public StudentReportResponse build() {
            return new StudentReportResponse(student, courses, totalEnrolledCourses, totalCredits, cumulativeGpa);
        }
    }

    public StudentResponse getStudent() { return student; }
    public void setStudent(StudentResponse student) { this.student = student; }

    public List<CoursePerformanceResponse> getCourses() { return courses; }
    public void setCourses(List<CoursePerformanceResponse> courses) { this.courses = courses; }

    public int getTotalEnrolledCourses() { return totalEnrolledCourses; }
    public void setTotalEnrolledCourses(int totalEnrolledCourses) { this.totalEnrolledCourses = totalEnrolledCourses; }

    public int getTotalCredits() { return totalCredits; }
    public void setTotalCredits(int totalCredits) { this.totalCredits = totalCredits; }

    public double getCumulativeGpa() { return cumulativeGpa; }
    public void setCumulativeGpa(double cumulativeGpa) { this.cumulativeGpa = cumulativeGpa; }
}
