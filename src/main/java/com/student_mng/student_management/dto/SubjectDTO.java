package com.student_mng.student_management.dto;

import com.student_mng.student_management.entity.Subject;

public record SubjectDTO(
        String id,
        String subjectName
) {
    public static SubjectDTO fromEntity(Subject subject) {
        return new SubjectDTO(
                subject.getId(),
                subject.getSubjectName()
        );
    }
}

