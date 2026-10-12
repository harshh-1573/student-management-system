package com.student.management.dto.response;

import com.student.management.util.AttendanceStatus;
import java.time.LocalDate;

public class AttendanceResponse {

    private Long id;
    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private String studentRollNumber;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private LocalDate date;
    private AttendanceStatus status;

    public AttendanceResponse() {}

    public AttendanceResponse(Long id, Long enrollmentId, Long studentId, String studentName, String studentRollNumber, Long courseId, String courseCode, String courseName, LocalDate date, AttendanceStatus status) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNumber = studentRollNumber;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.date = date;
        this.status = status;
    }

    public static AttendanceResponseBuilder builder() {
        return new AttendanceResponseBuilder();
    }

    public static class AttendanceResponseBuilder {
        private Long id;
        private Long enrollmentId;
        private Long studentId;
        private String studentName;
        private String studentRollNumber;
        private Long courseId;
        private String courseCode;
        private String courseName;
        private LocalDate date;
        private AttendanceStatus status;

        public AttendanceResponseBuilder id(Long id) { this.id = id; return this; }
        public AttendanceResponseBuilder enrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; return this; }
        public AttendanceResponseBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public AttendanceResponseBuilder studentName(String studentName) { this.studentName = studentName; return this; }
        public AttendanceResponseBuilder studentRollNumber(String studentRollNumber) { this.studentRollNumber = studentRollNumber; return this; }
        public AttendanceResponseBuilder courseId(Long courseId) { this.courseId = courseId; return this; }
        public AttendanceResponseBuilder courseCode(String courseCode) { this.courseCode = courseCode; return this; }
        public AttendanceResponseBuilder courseName(String courseName) { this.courseName = courseName; return this; }
        public AttendanceResponseBuilder date(LocalDate date) { this.date = date; return this; }
        public AttendanceResponseBuilder status(AttendanceStatus status) { this.status = status; return this; }

        public AttendanceResponse build() {
            return new AttendanceResponse(id, enrollmentId, studentId, studentName, studentRollNumber, courseId, courseCode, courseName, date, status);
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

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
}
