package com.student.management.service;

import com.student.management.dto.request.EnrollmentRequest;
import com.student.management.dto.response.EnrollmentResponse;
import com.student.management.dto.response.PageResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enrollStudent(EnrollmentRequest request);
    EnrollmentResponse dropStudent(Long enrollmentId, String currentUserEmail);
    PageResponse<EnrollmentResponse> getEnrollmentsByCourse(Long courseId, int page, int size);
    PageResponse<EnrollmentResponse> getEnrollmentsByStudent(Long studentId, int page, int size, String currentUserEmail);
    List<EnrollmentResponse> getMyEnrollments(String studentEmail);
}
