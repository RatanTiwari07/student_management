package com.student_mng.student_management.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomErrorResponse {
    private int status;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String path;

    public CustomErrorResponse(int status, String message, LocalDateTime timestamp, String path) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }
}