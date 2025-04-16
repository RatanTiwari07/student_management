package com.student_mng.student_management.dto;

import com.student_mng.student_management.enums.BatchType;

public record StudentDTO(
        String username,
        String email,
        String password,
        String rollNumber,
        String firstName,
        String lastName,
        String contactNumber,
        String parentContactNumber,
        String parentEmail,
        BatchType batch
) {}