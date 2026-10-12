package com.student.management.dto.request;

import com.student.management.util.ExamType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class MarkRequest {

    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;

    @NotNull(message = "Exam type is required")
    private ExamType examType;

    @NotNull(message = "Marks obtained is required")
    @DecimalMin(value = "0.0", message = "Marks obtained cannot be negative")
    private Double marksObtained;

    @NotNull(message = "Max marks is required")
    @DecimalMin(value = "1.0", message = "Max marks must be greater than 0")
    private Double maxMarks;

    public MarkRequest() {}

    public MarkRequest(Long enrollmentId, ExamType examType, Double marksObtained, Double maxMarks) {
        this.enrollmentId = enrollmentId;
        this.examType = examType;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
    }

    public static MarkRequestBuilder builder() {
        return new MarkRequestBuilder();
    }

    public static class MarkRequestBuilder {
        private Long enrollmentId;
        private ExamType examType;
        private Double marksObtained;
        private Double maxMarks;

        public MarkRequestBuilder enrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; return this; }
        public MarkRequestBuilder examType(ExamType examType) { this.examType = examType; return this; }
        public MarkRequestBuilder marksObtained(Double marksObtained) { this.marksObtained = marksObtained; return this; }
        public MarkRequestBuilder maxMarks(Double maxMarks) { this.maxMarks = maxMarks; return this; }

        public MarkRequest build() {
            return new MarkRequest(enrollmentId, examType, marksObtained, maxMarks);
        }
    }

    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

    public ExamType getExamType() { return examType; }
    public void setExamType(ExamType examType) { this.examType = examType; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public Double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Double maxMarks) { this.maxMarks = maxMarks; }

    public static class MarkBatchRequest {
        @NotNull(message = "Course ID is required")
        private Long courseId;

        @NotNull(message = "Exam type is required")
        private ExamType examType;

        @NotNull(message = "Max marks is required")
        @DecimalMin(value = "1.0", message = "Max marks must be greater than 0")
        private Double maxMarks;

        @NotEmpty(message = "Student marks list cannot be empty")
        @Valid
        private List<StudentMarkItem> items;

        public MarkBatchRequest() {}

        public MarkBatchRequest(Long courseId, ExamType examType, Double maxMarks, List<StudentMarkItem> items) {
            this.courseId = courseId;
            this.examType = examType;
            this.maxMarks = maxMarks;
            this.items = items;
        }

        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }

        public ExamType getExamType() { return examType; }
        public void setExamType(ExamType examType) { this.examType = examType; }

        public Double getMaxMarks() { return maxMarks; }
        public void setMaxMarks(Double maxMarks) { this.maxMarks = maxMarks; }

        public List<StudentMarkItem> getItems() { return items; }
        public void setItems(List<StudentMarkItem> items) { this.items = items; }
    }

    public static class StudentMarkItem {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Marks obtained is required")
        @DecimalMin(value = "0.0", message = "Marks obtained cannot be negative")
        private Double marksObtained;

        public StudentMarkItem() {}

        public StudentMarkItem(Long studentId, Double marksObtained) {
            this.studentId = studentId;
            this.marksObtained = marksObtained;
        }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public Double getMarksObtained() { return marksObtained; }
        public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }
    }
}
