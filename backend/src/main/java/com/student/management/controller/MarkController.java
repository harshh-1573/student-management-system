package com.student.management.controller;

import com.student.management.dto.request.MarkRequest;
import com.student.management.dto.response.ApiResponse;
import com.student.management.dto.response.MarkResponse;
import com.student.management.service.MarkService;
import com.student.management.util.ExamType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marks")
@Tag(name = "Marks & Grades", description = "Endpoints for recording and retrieving examination marks")
public class MarkController {

    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Record or update marks for a single student enrollment")
    public ResponseEntity<ApiResponse<MarkResponse>> saveMark(
            @Valid @RequestBody MarkRequest request,
            Authentication authentication) {
        MarkResponse response = markService.saveMark(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Mark saved successfully", response));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Record marks in batch for an exam type in a course")
    public ResponseEntity<ApiResponse<List<MarkResponse>>> saveMarkBatch(
            @Valid @RequestBody MarkRequest.MarkBatchRequest request,
            Authentication authentication) {
        List<MarkResponse> response = markService.saveMarkBatch(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Batch marks saved successfully", response));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get recorded marks for a course, optionally filtered by exam type")
    public ResponseEntity<ApiResponse<List<MarkResponse>>> getMarksByCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) ExamType examType,
            Authentication authentication) {
        List<MarkResponse> response = markService.getMarksByCourse(courseId, examType, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get marks for a specific enrollment")
    public ResponseEntity<ApiResponse<List<MarkResponse>>> getMarksByEnrollment(
            @PathVariable Long enrollmentId,
            Authentication authentication) {
        List<MarkResponse> response = markService.getMarksByEnrollment(enrollmentId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my-marks")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get all examination marks for logged in student")
    public ResponseEntity<ApiResponse<List<MarkResponse>>> getMyMarks(Authentication authentication) {
        List<MarkResponse> response = markService.getMyMarks(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
