package com.student_mng.student_management.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Resource not found: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {
        log.warn("Validation error: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<CustomErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {
        log.warn("Duplicate resource error: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<CustomErrorResponse> handleUnauthorizedAccessException(
            UnauthorizedAccessException ex,
            HttpServletRequest request) {
        log.warn("Unauthorized access: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<CustomErrorResponse> handleBusinessLogicException(
            BusinessLogicException ex,
            HttpServletRequest request) {
        log.warn("Business logic error: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<CustomErrorResponse> handleDatabaseOperationException(
            DatabaseOperationException ex,
            HttpServletRequest request) {
        log.error("Database operation error: {} at {}", ex.getMessage(), request.getRequestURI(), ex);
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Database operation failed. Please try again later.",
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<CustomErrorResponse> handleFileProcessingException(
            FileProcessingException ex,
            HttpServletRequest request) {
        log.warn("File processing error: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateAttendanceException.class)
    public ResponseEntity<CustomErrorResponse> handleDuplicateAttendanceException(
            DuplicateAttendanceException ex,
            HttpServletRequest request) {
        log.warn("Duplicate attendance error: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<CustomErrorResponse> handleInvalidDateException(
            InvalidDateException ex,
            HttpServletRequest request) {
        log.warn("Invalid date error: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {
        log.warn("Authentication failed: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Invalid username or password",
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        log.warn("Access denied: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Access denied. You don't have permission to access this resource.",
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        log.error("Data integrity violation: {} at {}", ex.getMessage(), request.getRequestURI());
        String message = "Data integrity constraint violation. Please check your input data.";

        // Extract more specific error messages for common constraints
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("email")) {
                message = "Email address already exists in the system.";
            } else if (ex.getMessage().contains("username")) {
                message = "Username already exists in the system.";
            } else if (ex.getMessage().contains("roll_number")) {
                message = "Roll number already exists in the system.";
            }
        }

        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.CONFLICT.value(),
            message,
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<CustomErrorResponse> handleDataAccessException(
            DataAccessException ex,
            HttpServletRequest request) {
        log.error("Database access error: {} at {}", ex.getMessage(), request.getRequestURI(), ex);
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Database operation failed. Please try again later.",
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<CustomErrorResponse> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request) {
        log.warn("File size exceeded: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "File size exceeds the maximum allowed limit.",
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        log.warn("Illegal argument: {} at {}", ex.getMessage(), request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        log.warn("Validation errors at {}: {}", request.getRequestURI(), ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error: {} at {}", ex.getMessage(), request.getRequestURI(), ex);
        CustomErrorResponse error = new CustomErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred. Please try again later.",
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}