package com.student_mng.student_management.dto;

import com.student_mng.student_management.entity.ClassEntity;

import java.util.List;

public record ClassResponseDTO(
        String id,
        String className,
        List<SubjectDTO> subjects
) {
    public static ClassResponseDTO fromEntity(ClassEntity classEntity) {
        return new ClassResponseDTO(
                classEntity.getId(),
                classEntity.getClassName(),
                classEntity.getSubjects().stream()
                        .map(SubjectDTO::fromEntity)
                        .toList()
        );
    }
}

