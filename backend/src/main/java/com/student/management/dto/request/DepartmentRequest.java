package com.student.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Department code is required")
    @Size(min = 2, max = 20, message = "Code must be between 2 and 20 characters")
    private String code;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    public DepartmentRequest() {}

    public DepartmentRequest(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public static DepartmentRequestBuilder builder() {
        return new DepartmentRequestBuilder();
    }

    public static class DepartmentRequestBuilder {
        private String name;
        private String code;
        private String description;

        public DepartmentRequestBuilder name(String name) { this.name = name; return this; }
        public DepartmentRequestBuilder code(String code) { this.code = code; return this; }
        public DepartmentRequestBuilder description(String description) { this.description = description; return this; }

        public DepartmentRequest build() {
            return new DepartmentRequest(name, code, description);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
