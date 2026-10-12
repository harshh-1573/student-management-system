package com.student.management.dto.response;

import java.time.LocalDate;

public class StudentResponse {

    private Long id;
    private String rollNumber;
    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
    private Integer enrollmentYear;
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private Long userId;

    public StudentResponse() {}

    public StudentResponse(Long id, String rollNumber, String name, String email, String phone, LocalDate dateOfBirth, String address, Integer enrollmentYear, Long departmentId, String departmentName, String departmentCode, Long userId) {
        this.id = id;
        this.rollNumber = rollNumber;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.enrollmentYear = enrollmentYear;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.userId = userId;
    }

    public static StudentResponseBuilder builder() {
        return new StudentResponseBuilder();
    }

    public static class StudentResponseBuilder {
        private Long id;
        private String rollNumber;
        private String name;
        private String email;
        private String phone;
        private LocalDate dateOfBirth;
        private String address;
        private Integer enrollmentYear;
        private Long departmentId;
        private String departmentName;
        private String departmentCode;
        private Long userId;

        public StudentResponseBuilder id(Long id) { this.id = id; return this; }
        public StudentResponseBuilder rollNumber(String rollNumber) { this.rollNumber = rollNumber; return this; }
        public StudentResponseBuilder name(String name) { this.name = name; return this; }
        public StudentResponseBuilder email(String email) { this.email = email; return this; }
        public StudentResponseBuilder phone(String phone) { this.phone = phone; return this; }
        public StudentResponseBuilder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public StudentResponseBuilder address(String address) { this.address = address; return this; }
        public StudentResponseBuilder enrollmentYear(Integer enrollmentYear) { this.enrollmentYear = enrollmentYear; return this; }
        public StudentResponseBuilder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public StudentResponseBuilder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public StudentResponseBuilder departmentCode(String departmentCode) { this.departmentCode = departmentCode; return this; }
        public StudentResponseBuilder userId(Long userId) { this.userId = userId; return this; }

        public StudentResponse build() {
            return new StudentResponse(id, rollNumber, name, email, phone, dateOfBirth, address, enrollmentYear, departmentId, departmentName, departmentCode, userId);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
