package com.student.management.service;

import com.student.management.dto.request.MarkRequest;
import com.student.management.dto.response.MarkResponse;
import com.student.management.util.ExamType;

import java.util.List;

public interface MarkService {
    MarkResponse saveMark(MarkRequest request, String teacherEmail);
    List<MarkResponse> saveMarkBatch(MarkRequest.MarkBatchRequest request, String teacherEmail);
    List<MarkResponse> getMarksByCourse(Long courseId, ExamType examType, String teacherEmail);
    List<MarkResponse> getMarksByEnrollment(Long enrollmentId, String currentUserEmail);
    List<MarkResponse> getMyMarks(String studentEmail);
}
