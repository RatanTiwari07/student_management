package com.student_mng.student_management.dto;

import java.util.List;

public record AssignStudentsDTO(
        List<String> studentIds,
        String classId
) {}
