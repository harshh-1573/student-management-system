package com.student.management.dto.response;

public class AttendanceStatsResponse {

    private Long studentId;
    private String studentName;
    private String studentRollNumber;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private long totalClasses;
    private long presentClasses;
    private long absentClasses;
    private long lateClasses;
    private double attendancePercentage;

    public AttendanceStatsResponse() {}

    public AttendanceStatsResponse(Long studentId, String studentName, String studentRollNumber, Long courseId, String courseCode, String courseName, long totalClasses, long presentClasses, long absentClasses, long lateClasses, double attendancePercentage) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNumber = studentRollNumber;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.totalClasses = totalClasses;
        this.presentClasses = presentClasses;
        this.absentClasses = absentClasses;
        this.lateClasses = lateClasses;
        this.attendancePercentage = attendancePercentage;
    }

    public static AttendanceStatsResponseBuilder builder() {
        return new AttendanceStatsResponseBuilder();
    }

    public static class AttendanceStatsResponseBuilder {
        private Long studentId;
        private String studentName;
        private String studentRollNumber;
        private Long courseId;
        private String courseCode;
        private String courseName;
        private long totalClasses;
        private long presentClasses;
        private long absentClasses;
        private long lateClasses;
        private double attendancePercentage;

        public AttendanceStatsResponseBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public AttendanceStatsResponseBuilder studentName(String studentName) { this.studentName = studentName; return this; }
        public AttendanceStatsResponseBuilder studentRollNumber(String studentRollNumber) { this.studentRollNumber = studentRollNumber; return this; }
        public AttendanceStatsResponseBuilder courseId(Long courseId) { this.courseId = courseId; return this; }
        public AttendanceStatsResponseBuilder courseCode(String courseCode) { this.courseCode = courseCode; return this; }
        public AttendanceStatsResponseBuilder courseName(String courseName) { this.courseName = courseName; return this; }
        public AttendanceStatsResponseBuilder totalClasses(long totalClasses) { this.totalClasses = totalClasses; return this; }
        public AttendanceStatsResponseBuilder presentClasses(long presentClasses) { this.presentClasses = presentClasses; return this; }
        public AttendanceStatsResponseBuilder absentClasses(long absentClasses) { this.absentClasses = absentClasses; return this; }
        public AttendanceStatsResponseBuilder lateClasses(long lateClasses) { this.lateClasses = lateClasses; return this; }
        public AttendanceStatsResponseBuilder attendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; return this; }

        public AttendanceStatsResponse build() {
            return new AttendanceStatsResponse(studentId, studentName, studentRollNumber, courseId, courseCode, courseName, totalClasses, presentClasses, absentClasses, lateClasses, attendancePercentage);
        }
    }

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

    public long getTotalClasses() { return totalClasses; }
    public void setTotalClasses(long totalClasses) { this.totalClasses = totalClasses; }

    public long getPresentClasses() { return presentClasses; }
    public void setPresentClasses(long presentClasses) { this.presentClasses = presentClasses; }

    public long getAbsentClasses() { return absentClasses; }
    public void setAbsentClasses(long absentClasses) { this.absentClasses = absentClasses; }

    public long getLateClasses() { return lateClasses; }
    public void setLateClasses(long lateClasses) { this.lateClasses = lateClasses; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
}
