package com.student.management.controller;

import com.student.management.dto.request.AttendanceBatchRequest;
import com.student.management.dto.response.ApiResponse;
import com.student.management.dto.response.AttendanceResponse;
import com.student.management.dto.response.AttendanceStatsResponse;
import com.student.management.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@Tag(name = "Attendance", description = "Endpoints for tracking and analyzing course attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Mark or update attendance batch for a course on a specific date")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> markAttendanceBatch(
            @Valid @RequestBody AttendanceBatchRequest request,
            Authentication authentication) {
        List<AttendanceResponse> response = attendanceService.markAttendanceBatch(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Attendance recorded successfully", response));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get attendance records for a course on a specific date")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendanceByCourseAndDate(
            @PathVariable Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        List<AttendanceResponse> response = attendanceService.getAttendanceByCourseAndDate(courseId, date, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/course/{courseId}/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get aggregate attendance statistics and percentage for each student in a course")
    public ResponseEntity<ApiResponse<List<AttendanceStatsResponse>>> getCourseAttendanceStats(
            @PathVariable Long courseId,
            Authentication authentication) {
        List<AttendanceStatsResponse> response = attendanceService.getCourseAttendanceStats(courseId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my-attendance")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get all attendance records for logged in student")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getMyAttendance(Authentication authentication) {
        List<AttendanceResponse> response = attendanceService.getMyAttendance(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
