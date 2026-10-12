package com.student.management.controller;

import com.student.management.dto.request.EnrollmentRequest;
import com.student.management.dto.response.ApiResponse;
import com.student.management.dto.response.EnrollmentResponse;
import com.student.management.dto.response.PageResponse;
import com.student.management.service.EnrollmentService;
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
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments", description = "Endpoints for course enrollment and dropping")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @Operation(summary = "Enroll student in course (ADMIN or STUDENT)")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollStudent(@Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.enrollStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Student successfully enrolled in course", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @Operation(summary = "Drop course enrollment (ADMIN or STUDENT for own enrollment)")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> dropStudent(
            @PathVariable Long id,
            Authentication authentication) {
        EnrollmentResponse response = enrollmentService.dropStudent(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Enrollment dropped successfully", response));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get enrollments for a course")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponse>>> getEnrollmentsByCourse(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        PageResponse<EnrollmentResponse> response = enrollmentService.getEnrollmentsByCourse(courseId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get enrollments for a student")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponse>>> getEnrollmentsByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            Authentication authentication) {
        PageResponse<EnrollmentResponse> response = enrollmentService.getEnrollmentsByStudent(studentId, page, size, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my-enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get active course enrollments for the logged in student")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(Authentication authentication) {
        List<EnrollmentResponse> response = enrollmentService.getMyEnrollments(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
