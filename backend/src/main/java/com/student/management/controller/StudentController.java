package com.student.management.controller;

import com.student.management.dto.request.StudentRequest;
import com.student.management.dto.response.ApiResponse;
import com.student.management.dto.response.PageResponse;
import com.student.management.dto.response.StudentReportResponse;
import com.student.management.dto.response.StudentResponse;
import com.student.management.service.StudentService;
import com.student.management.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Endpoints for student management and academic reports")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create student with user account (ADMIN only)")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Student created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @Operation(summary = "Update student (ADMIN or STUDENT for own profile)")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request,
            Authentication authentication) {
        StudentResponse response = studentService.updateStudent(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete student (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
            @PathVariable Long id,
            Authentication authentication) {
        StudentResponse response = studentService.getStudentById(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get currently authenticated student profile")
    public ResponseEntity<ApiResponse<StudentResponse>> getMyStudentProfile(Authentication authentication) {
        StudentResponse response = studentService.getStudentByEmail(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/report")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get complete academic report for student (courses, attendance %, marks, GPA)")
    public ResponseEntity<ApiResponse<StudentReportResponse>> getStudentReport(
            @PathVariable Long id,
            Authentication authentication) {
        StudentReportResponse response = studentService.getStudentReport(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/report")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get own academic report for logged in student")
    public ResponseEntity<ApiResponse<StudentReportResponse>> getMyStudentReport(Authentication authentication) {
        StudentResponse student = studentService.getStudentByEmail(authentication.getName());
        StudentReportResponse response = studentService.getStudentReport(student.getId(), authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get all students paginated and searchable")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getAllStudents(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir,
            @RequestParam(required = false) String search) {
        PageResponse<StudentResponse> response = studentService.getAllStudents(page, size, sortBy, sortDir, search);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get unpaginated list of students for dropdown selectors")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudentsList() {
        List<StudentResponse> response = studentService.getAllStudentsList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
