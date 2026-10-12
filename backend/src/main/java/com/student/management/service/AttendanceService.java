package com.student.management.service;

import com.student.management.dto.request.AttendanceBatchRequest;
import com.student.management.dto.response.AttendanceResponse;
import com.student.management.dto.response.AttendanceStatsResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    List<AttendanceResponse> markAttendanceBatch(AttendanceBatchRequest request, String teacherEmail);
    List<AttendanceResponse> getAttendanceByCourseAndDate(Long courseId, LocalDate date, String teacherEmail);
    List<AttendanceStatsResponse> getCourseAttendanceStats(Long courseId, String teacherEmail);
    List<AttendanceResponse> getMyAttendance(String studentEmail);
}
