package com.student_mng.student_management.dto;

import com.student_mng.student_management.enums.BatchType;
import com.student_mng.student_management.enums.LectureType;

import java.util.Objects;

public record TeacherAssignmentDTO(
    String teacherId,
    String classId,
    String subjectId,
    LectureType lectureType,
    BatchType batch,
    String lectureSlotId
) {
    // Add validation annotations
    public TeacherAssignmentDTO {
        Objects.requireNonNull(teacherId, "Teacher ID cannot be null");
        Objects.requireNonNull(classId, "Class ID cannot be null");
        Objects.requireNonNull(subjectId, "Subject ID cannot be null");
        Objects.requireNonNull(lectureType, "Lecture type cannot be null");
    }
}