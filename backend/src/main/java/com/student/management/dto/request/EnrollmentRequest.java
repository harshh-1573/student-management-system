package com.student.management.dto.request;

import jakarta.validation.constraints.NotNull;

public class EnrollmentRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    public EnrollmentRequest() {}

    public EnrollmentRequest(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public static EnrollmentRequestBuilder builder() {
        return new EnrollmentRequestBuilder();
    }

    public static class EnrollmentRequestBuilder {
        private Long studentId;
        private Long courseId;

        public EnrollmentRequestBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public EnrollmentRequestBuilder courseId(Long courseId) { this.courseId = courseId; return this; }

        public EnrollmentRequest build() {
            return new EnrollmentRequest(studentId, courseId);
        }
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
}
