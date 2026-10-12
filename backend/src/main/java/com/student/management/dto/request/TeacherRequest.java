package com.student.management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TeacherRequest {

    @NotBlank(message = "Teacher name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    private String phone;
    private String qualification;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String password;

    public TeacherRequest() {}

    public TeacherRequest(String name, String email, String phone, String qualification, Long departmentId, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.qualification = qualification;
        this.departmentId = departmentId;
        this.password = password;
    }

    public static TeacherRequestBuilder builder() {
        return new TeacherRequestBuilder();
    }

    public static class TeacherRequestBuilder {
        private String name;
        private String email;
        private String phone;
        private String qualification;
        private Long departmentId;
        private String password;

        public TeacherRequestBuilder name(String name) { this.name = name; return this; }
        public TeacherRequestBuilder email(String email) { this.email = email; return this; }
        public TeacherRequestBuilder phone(String phone) { this.phone = phone; return this; }
        public TeacherRequestBuilder qualification(String qualification) { this.qualification = qualification; return this; }
        public TeacherRequestBuilder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public TeacherRequestBuilder password(String password) { this.password = password; return this; }

        public TeacherRequest build() {
            return new TeacherRequest(name, email, phone, qualification, departmentId, password);
        }
    }

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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
