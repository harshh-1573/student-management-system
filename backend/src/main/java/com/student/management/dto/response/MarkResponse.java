package com.student.management.dto.response;

import com.student.management.util.ExamType;

public class MarkResponse {

    private Long id;
    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private String studentRollNumber;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private ExamType examType;
    private Double marksObtained;
    private Double maxMarks;
    private Double percentage;

    public MarkResponse() {}

    public MarkResponse(Long id, Long enrollmentId, Long studentId, String studentName, String studentRollNumber, Long courseId, String courseCode, String courseName, ExamType examType, Double marksObtained, Double maxMarks, Double percentage) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNumber = studentRollNumber;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.examType = examType;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
        this.percentage = percentage;
    }

    public static MarkResponseBuilder builder() {
        return new MarkResponseBuilder();
    }

    public static class MarkResponseBuilder {
        private Long id;
        private Long enrollmentId;
        private Long studentId;
        private String studentName;
        private String studentRollNumber;
        private Long courseId;
        private String courseCode;
        private String courseName;
        private ExamType examType;
        private Double marksObtained;
        private Double maxMarks;
        private Double percentage;

        public MarkResponseBuilder id(Long id) { this.id = id; return this; }
        public MarkResponseBuilder enrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; return this; }
        public MarkResponseBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public MarkResponseBuilder studentName(String studentName) { this.studentName = studentName; return this; }
        public MarkResponseBuilder studentRollNumber(String studentRollNumber) { this.studentRollNumber = studentRollNumber; return this; }
        public MarkResponseBuilder courseId(Long courseId) { this.courseId = courseId; return this; }
        public MarkResponseBuilder courseCode(String courseCode) { this.courseCode = courseCode; return this; }
        public MarkResponseBuilder courseName(String courseName) { this.courseName = courseName; return this; }
        public MarkResponseBuilder examType(ExamType examType) { this.examType = examType; return this; }
        public MarkResponseBuilder marksObtained(Double marksObtained) { this.marksObtained = marksObtained; return this; }
        public MarkResponseBuilder maxMarks(Double maxMarks) { this.maxMarks = maxMarks; return this; }
        public MarkResponseBuilder percentage(Double percentage) { this.percentage = percentage; return this; }

        public MarkResponse build() {
            return new MarkResponse(id, enrollmentId, studentId, studentName, studentRollNumber, courseId, courseCode, courseName, examType, marksObtained, maxMarks, percentage);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentRollNumber() { return studentRollNumber; }
    public void setStudentRollNumber(String studentRollNumber) { this.studentRollNumber = studentRollNumber; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public ExamType getExamType() { return examType; }
    public void setExamType(ExamType examType) { this.examType = examType; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public Double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Double maxMarks) { this.maxMarks = maxMarks; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
}
