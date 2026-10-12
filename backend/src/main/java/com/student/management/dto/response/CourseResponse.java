package com.student.management.dto.response;

public class CourseResponse {

    private Long id;
    private String code;
    private String name;
    private Integer credits;
    private String semester;
    private Long departmentId;
    private String departmentName;
    private Long teacherId;
    private String teacherName;
    private int enrolledStudentCount;

    public CourseResponse() {}

    public CourseResponse(Long id, String code, String name, Integer credits, String semester, Long departmentId, String departmentName, Long teacherId, String teacherName, int enrolledStudentCount) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.semester = semester;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.enrolledStudentCount = enrolledStudentCount;
    }

    public static CourseResponseBuilder builder() {
        return new CourseResponseBuilder();
    }

    public static class CourseResponseBuilder {
        private Long id;
        private String code;
        private String name;
        private Integer credits;
        private String semester;
        private Long departmentId;
        private String departmentName;
        private Long teacherId;
        private String teacherName;
        private int enrolledStudentCount;

        public CourseResponseBuilder id(Long id) { this.id = id; return this; }
        public CourseResponseBuilder code(String code) { this.code = code; return this; }
        public CourseResponseBuilder name(String name) { this.name = name; return this; }
        public CourseResponseBuilder credits(Integer credits) { this.credits = credits; return this; }
        public CourseResponseBuilder semester(String semester) { this.semester = semester; return this; }
        public CourseResponseBuilder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public CourseResponseBuilder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public CourseResponseBuilder teacherId(Long teacherId) { this.teacherId = teacherId; return this; }
        public CourseResponseBuilder teacherName(String teacherName) { this.teacherName = teacherName; return this; }
        public CourseResponseBuilder enrolledStudentCount(int enrolledStudentCount) { this.enrolledStudentCount = enrolledStudentCount; return this; }

        public CourseResponse build() {
            return new CourseResponse(id, code, name, credits, semester, departmentId, departmentName, teacherId, teacherName, enrolledStudentCount);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public int getEnrolledStudentCount() { return enrolledStudentCount; }
    public void setEnrolledStudentCount(int enrolledStudentCount) { this.enrolledStudentCount = enrolledStudentCount; }
}
