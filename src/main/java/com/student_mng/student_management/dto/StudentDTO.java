package com.student_mng.student_management.dto;

public record StudentDTO(
        String username,
        String email,
        String password,
        String rollNumber,
        String classId  // ClassEntity ID
) {}

