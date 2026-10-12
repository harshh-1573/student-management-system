package com.student.management.service;

import com.student.management.dto.request.TeacherRequest;
import com.student.management.dto.response.PageResponse;
import com.student.management.dto.response.TeacherResponse;

import java.util.List;

public interface TeacherService {
    TeacherResponse createTeacher(TeacherRequest request);
    TeacherResponse updateTeacher(Long id, TeacherRequest request);
    void deleteTeacher(Long id);
    TeacherResponse getTeacherById(Long id);
    TeacherResponse getTeacherByEmail(String email);
    PageResponse<TeacherResponse> getAllTeachers(int page, int size, String sortBy, String sortDir, String search);
    List<TeacherResponse> getAllTeachersList();
}
