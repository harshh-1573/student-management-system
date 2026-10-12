package com.student.management.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class StudentRequest {

    @NotBlank(message = "Roll number is required")
    @Size(min = 2, max = 30, message = "Roll number must be between 2 and 30 characters")
    private String rollNumber;

    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    private String phone;
    private LocalDate dateOfBirth;
    private String address;

    @NotNull(message = "Enrollment year is required")
    @Min(value = 2000, message = "Enrollment year must be valid")
    private Integer enrollmentYear;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String password;

    public StudentRequest() {}

    public StudentRequest(String rollNumber, String name, String email, String phone, LocalDate dateOfBirth, String address, Integer enrollmentYear, Long departmentId, String password) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.enrollmentYear = enrollmentYear;
        this.departmentId = departmentId;
        this.password = password;
    }

    public static StudentRequestBuilder builder() {
        return new StudentRequestBuilder();
    }

    public static class StudentRequestBuilder {
        private String rollNumber;
        private String name;
        private String email;
        private String phone;
        private LocalDate dateOfBirth;
        private String address;
        private Integer enrollmentYear;
        private Long departmentId;
        private String password;

        public StudentRequestBuilder rollNumber(String rollNumber) { this.rollNumber = rollNumber; return this; }
        public StudentRequestBuilder name(String name) { this.name = name; return this; }
        public StudentRequestBuilder email(String email) { this.email = email; return this; }
        public StudentRequestBuilder phone(String phone) { this.phone = phone; return this; }
        public StudentRequestBuilder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public StudentRequestBuilder address(String address) { this.address = address; return this; }
        public StudentRequestBuilder enrollmentYear(Integer enrollmentYear) { this.enrollmentYear = enrollmentYear; return this; }
        public StudentRequestBuilder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public StudentRequestBuilder password(String password) { this.password = password; return this; }

        public StudentRequest build() {
            return new StudentRequest(rollNumber, name, email, phone, dateOfBirth, address, enrollmentYear, departmentId, password);
        }
    }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getEnrollmentYear() { return enrollmentYear; }
    public void setEnrollmentYear(Integer enrollmentYear) { this.enrollmentYear = enrollmentYear; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
