package com.student.management.dto.response;

public class TeacherResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String qualification;
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private Long userId;

    public TeacherResponse() {}

    public TeacherResponse(Long id, String name, String email, String phone, String qualification, Long departmentId, String departmentName, String departmentCode, Long userId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.qualification = qualification;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.userId = userId;
    }

    public static TeacherResponseBuilder builder() {
        return new TeacherResponseBuilder();
    }

    public static class TeacherResponseBuilder {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String qualification;
        private Long departmentId;
        private String departmentName;
        private String departmentCode;
        private Long userId;

        public TeacherResponseBuilder id(Long id) { this.id = id; return this; }
        public TeacherResponseBuilder name(String name) { this.name = name; return this; }
        public TeacherResponseBuilder email(String email) { this.email = email; return this; }
        public TeacherResponseBuilder phone(String phone) { this.phone = phone; return this; }
        public TeacherResponseBuilder qualification(String qualification) { this.qualification = qualification; return this; }
        public TeacherResponseBuilder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public TeacherResponseBuilder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public TeacherResponseBuilder departmentCode(String departmentCode) { this.departmentCode = departmentCode; return this; }
        public TeacherResponseBuilder userId(Long userId) { this.userId = userId; return this; }

        public TeacherResponse build() {
            return new TeacherResponse(id, name, email, phone, qualification, departmentId, departmentName, departmentCode, userId);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
