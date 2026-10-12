package com.student.management.dto.request;

import jakarta.validation.constraints.*;

public class CourseRequest {

    @NotBlank(message = "Course code is required")
    @Size(min = 2, max = 30, message = "Code must be between 2 and 30 characters")
    private String code;

    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 150, message = "Course name must be between 2 and 150 characters")
    private String name;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits cannot exceed 10")
    private Integer credits;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private Long teacherId;

    public CourseRequest() {}

    public CourseRequest(String code, String name, Integer credits, String semester, Long departmentId, Long teacherId) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.semester = semester;
        this.departmentId = departmentId;
        this.teacherId = teacherId;
    }

    public static CourseRequestBuilder builder() {
        return new CourseRequestBuilder();
    }

    public static class CourseRequestBuilder {
        private String code;
        private String name;
        private Integer credits;
        private String semester;
        private Long departmentId;
        private Long teacherId;

        public CourseRequestBuilder code(String code) { this.code = code; return this; }
        public CourseRequestBuilder name(String name) { this.name = name; return this; }
        public CourseRequestBuilder credits(Integer credits) { this.credits = credits; return this; }
        public CourseRequestBuilder semester(String semester) { this.semester = semester; return this; }
        public CourseRequestBuilder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public CourseRequestBuilder teacherId(Long teacherId) { this.teacherId = teacherId; return this; }

        public CourseRequest build() {
            return new CourseRequest(code, name, credits, semester, departmentId, teacherId);
        }
    }

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

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
}
