package com.student.management.dto.response;

import java.util.ArrayList;
import java.util.List;

public class CoursePerformanceResponse {

    private Long courseId;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private String semester;
    private double attendancePercentage;
    private List<MarkResponse> marks = new ArrayList<>();
    private double totalMarksObtained;
    private double totalMaxMarks;
    private double overallPercentage;
    private String grade;
    private double gradePoint;

    public CoursePerformanceResponse() {}

    public CoursePerformanceResponse(Long courseId, String courseCode, String courseName, Integer credits, String semester, double attendancePercentage, List<MarkResponse> marks, double totalMarksObtained, double totalMaxMarks, double overallPercentage, String grade, double gradePoint) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.attendancePercentage = attendancePercentage;
        this.marks = marks != null ? marks : new ArrayList<>();
        this.totalMarksObtained = totalMarksObtained;
        this.totalMaxMarks = totalMaxMarks;
        this.overallPercentage = overallPercentage;
        this.grade = grade;
        this.gradePoint = gradePoint;
    }

    public static CoursePerformanceResponseBuilder builder() {
        return new CoursePerformanceResponseBuilder();
    }

    public static class CoursePerformanceResponseBuilder {
        private Long courseId;
        private String courseCode;
        private String courseName;
        private Integer credits;
        private String semester;
        private double attendancePercentage;
        private List<MarkResponse> marks = new ArrayList<>();
        private double totalMarksObtained;
        private double totalMaxMarks;
        private double overallPercentage;
        private String grade;
        private double gradePoint;

        public CoursePerformanceResponseBuilder courseId(Long courseId) { this.courseId = courseId; return this; }
        public CoursePerformanceResponseBuilder courseCode(String courseCode) { this.courseCode = courseCode; return this; }
        public CoursePerformanceResponseBuilder courseName(String courseName) { this.courseName = courseName; return this; }
        public CoursePerformanceResponseBuilder credits(Integer credits) { this.credits = credits; return this; }
        public CoursePerformanceResponseBuilder semester(String semester) { this.semester = semester; return this; }
        public CoursePerformanceResponseBuilder attendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; return this; }
        public CoursePerformanceResponseBuilder marks(List<MarkResponse> marks) { this.marks = marks; return this; }
        public CoursePerformanceResponseBuilder totalMarksObtained(double totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; return this; }
        public CoursePerformanceResponseBuilder totalMaxMarks(double totalMaxMarks) { this.totalMaxMarks = totalMaxMarks; return this; }
        public CoursePerformanceResponseBuilder overallPercentage(double overallPercentage) { this.overallPercentage = overallPercentage; return this; }
        public CoursePerformanceResponseBuilder grade(String grade) { this.grade = grade; return this; }
        public CoursePerformanceResponseBuilder gradePoint(double gradePoint) { this.gradePoint = gradePoint; return this; }

        public CoursePerformanceResponse build() {
            return new CoursePerformanceResponse(courseId, courseCode, courseName, credits, semester, attendancePercentage, marks, totalMarksObtained, totalMaxMarks, overallPercentage, grade, gradePoint);
        }
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public List<MarkResponse> getMarks() { return marks; }
    public void setMarks(List<MarkResponse> marks) { this.marks = marks; }

    public double getTotalMarksObtained() { return totalMarksObtained; }
    public void setTotalMarksObtained(double totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; }

    public double getTotalMaxMarks() { return totalMaxMarks; }
    public void setTotalMaxMarks(double totalMaxMarks) { this.totalMaxMarks = totalMaxMarks; }

    public double getOverallPercentage() { return overallPercentage; }
    public void setOverallPercentage(double overallPercentage) { this.overallPercentage = overallPercentage; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public double getGradePoint() { return gradePoint; }
    public void setGradePoint(double gradePoint) { this.gradePoint = gradePoint; }
}
