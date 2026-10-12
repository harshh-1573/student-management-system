package com.student.management.service;

import com.student.management.dto.request.StudentRequest;
import com.student.management.dto.response.PageResponse;
import com.student.management.dto.response.StudentReportResponse;
import com.student.management.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    StudentResponse updateStudent(Long id, StudentRequest request, String currentUserEmail);
    void deleteStudent(Long id);
    StudentResponse getStudentById(Long id, String currentUserEmail);
    StudentResponse getStudentByEmail(String email);
    PageResponse<StudentResponse> getAllStudents(int page, int size, String sortBy, String sortDir, String search);
    List<StudentResponse> getAllStudentsList();
    StudentReportResponse getStudentReport(Long studentId, String currentUserEmail);
}
