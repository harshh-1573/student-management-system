package com.student.management.service;

import com.student.management.dto.request.CourseRequest;
import com.student.management.dto.response.CourseResponse;
import com.student.management.dto.response.PageResponse;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    CourseResponse updateCourse(Long id, CourseRequest request);
    void deleteCourse(Long id);
    CourseResponse getCourseById(Long id);
    PageResponse<CourseResponse> getAllCourses(int page, int size, String sortBy, String sortDir, String search);
    List<CourseResponse> getCoursesByTeacher(String teacherEmail);
    List<CourseResponse> getAllCoursesList();
}
