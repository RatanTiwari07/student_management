package com.student_mng.student_management.dto;

import java.time.LocalDate;
import java.util.List;

public record AttendanceSubmissionDTO(
    String teacherAssignmentId,
    LocalDate date,
    List<AttendanceRecord> attendanceRecords
) {
    public record AttendanceRecord(
        String studentId,
        boolean present
    ) {}
}