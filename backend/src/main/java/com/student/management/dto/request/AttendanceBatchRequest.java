package com.student.management.dto.request;

import com.student.management.util.AttendanceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class AttendanceBatchRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Attendance date is required")
    private LocalDate date;

    @NotEmpty(message = "Attendance records cannot be empty")
    @Valid
    private List<AttendanceItem> records;

    public AttendanceBatchRequest() {}

    public AttendanceBatchRequest(Long courseId, LocalDate date, List<AttendanceItem> records) {
        this.courseId = courseId;
        this.date = date;
        this.records = records;
    }

    public static AttendanceBatchRequestBuilder builder() {
        return new AttendanceBatchRequestBuilder();
    }

    public static class AttendanceBatchRequestBuilder {
        private Long courseId;
        private LocalDate date;
        private List<AttendanceItem> records;

        public AttendanceBatchRequestBuilder courseId(Long courseId) { this.courseId = courseId; return this; }
        public AttendanceBatchRequestBuilder date(LocalDate date) { this.date = date; return this; }
        public AttendanceBatchRequestBuilder records(List<AttendanceItem> records) { this.records = records; return this; }

        public AttendanceBatchRequest build() {
            return new AttendanceBatchRequest(courseId, date, records);
        }
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<AttendanceItem> getRecords() { return records; }
    public void setRecords(List<AttendanceItem> records) { this.records = records; }

    public static class AttendanceItem {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Attendance status is required")
        private AttendanceStatus status;

        public AttendanceItem() {}

        public AttendanceItem(Long studentId, AttendanceStatus status) {
            this.studentId = studentId;
            this.status = status;
        }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public AttendanceStatus getStatus() { return status; }
        public void setStatus(AttendanceStatus status) { this.status = status; }
    }
}
