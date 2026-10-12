package com.student.management.service;

import com.student.management.dto.request.DepartmentRequest;
import com.student.management.dto.response.DepartmentResponse;
import com.student.management.dto.response.PageResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse createDepartment(DepartmentRequest request);
    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);
    void deleteDepartment(Long id);
    DepartmentResponse getDepartmentById(Long id);
    PageResponse<DepartmentResponse> getAllDepartments(int page, int size, String sortBy, String sortDir, String search);
    List<DepartmentResponse> getAllDepartmentsList();
}
