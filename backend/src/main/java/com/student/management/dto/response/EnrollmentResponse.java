package com.student.management.dto.response;

import com.student.management.util.EnrollmentStatus;
import java.time.LocalDate;

public class EnrollmentResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRollNumber;
    private String studentEmail;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private String semester;
    private LocalDate enrollmentDate;
    private EnrollmentStatus status;

    public EnrollmentResponse() {}

    public EnrollmentResponse(Long id, Long studentId, String studentName, String studentRollNumber, String studentEmail, Long courseId, String courseCode, String courseName, Integer credits, String semester, LocalDate enrollmentDate, EnrollmentStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNumber = studentRollNumber;
        this.studentEmail = studentEmail;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
    }

    public static EnrollmentResponseBuilder builder() {
        return new EnrollmentResponseBuilder();
    }

    public static class EnrollmentResponseBuilder {
        private Long id;
        private Long studentId;
        private String studentName;
        private String studentRollNumber;
        private String studentEmail;
        private Long courseId;
        private String courseCode;
        private String courseName;
        private Integer credits;
        private String semester;
        private LocalDate enrollmentDate;
        private EnrollmentStatus status;

        public EnrollmentResponseBuilder id(Long id) { this.id = id; return this; }
        public EnrollmentResponseBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public EnrollmentResponseBuilder studentName(String studentName) { this.studentName = studentName; return this; }
        public EnrollmentResponseBuilder studentRollNumber(String studentRollNumber) { this.studentRollNumber = studentRollNumber; return this; }
        public EnrollmentResponseBuilder studentEmail(String studentEmail) { this.studentEmail = studentEmail; return this; }
        public EnrollmentResponseBuilder courseId(Long courseId) { this.courseId = courseId; return this; }
        public EnrollmentResponseBuilder courseCode(String courseCode) { this.courseCode = courseCode; return this; }
        public EnrollmentResponseBuilder courseName(String courseName) { this.courseName = courseName; return this; }
        public EnrollmentResponseBuilder credits(Integer credits) { this.credits = credits; return this; }
        public EnrollmentResponseBuilder semester(String semester) { this.semester = semester; return this; }
        public EnrollmentResponseBuilder enrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; return this; }
        public EnrollmentResponseBuilder status(EnrollmentStatus status) { this.status = status; return this; }

        public EnrollmentResponse build() {
            return new EnrollmentResponse(id, studentId, studentName, studentRollNumber, studentEmail, courseId, courseCode, courseName, credits, semester, enrollmentDate, status);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentRollNumber() { return studentRollNumber; }
    public void setStudentRollNumber(String studentRollNumber) { this.studentRollNumber = studentRollNumber; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

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

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }
}
