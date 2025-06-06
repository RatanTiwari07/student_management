package com.student_mng.student_management.dto;

import java.util.List;

public record ClassDTO(
        String className,
        List<String> subjectIds  // Optional: if you want to assign subjects during creation
) {}