package com.student.management.dto.response;

public class DepartmentResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private int studentCount;
    private int courseCount;
    private int teacherCount;

    public DepartmentResponse() {}

    public DepartmentResponse(Long id, String name, String code, String description, int studentCount, int courseCount, int teacherCount) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.studentCount = studentCount;
        this.courseCount = courseCount;
        this.teacherCount = teacherCount;
    }

    public static DepartmentResponseBuilder builder() {
        return new DepartmentResponseBuilder();
    }

    public static class DepartmentResponseBuilder {
        private Long id;
        private String name;
        private String code;
        private String description;
        private int studentCount;
        private int courseCount;
        private int teacherCount;

        public DepartmentResponseBuilder id(Long id) { this.id = id; return this; }
        public DepartmentResponseBuilder name(String name) { this.name = name; return this; }
        public DepartmentResponseBuilder code(String code) { this.code = code; return this; }
        public DepartmentResponseBuilder description(String description) { this.description = description; return this; }
        public DepartmentResponseBuilder studentCount(int studentCount) { this.studentCount = studentCount; return this; }
        public DepartmentResponseBuilder courseCount(int courseCount) { this.courseCount = courseCount; return this; }
        public DepartmentResponseBuilder teacherCount(int teacherCount) { this.teacherCount = teacherCount; return this; }

        public DepartmentResponse build() {
            return new DepartmentResponse(id, name, code, description, studentCount, courseCount, teacherCount);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public int getCourseCount() { return courseCount; }
    public void setCourseCount(int courseCount) { this.courseCount = courseCount; }

    public int getTeacherCount() { return teacherCount; }
    public void setTeacherCount(int teacherCount) { this.teacherCount = teacherCount; }
}
