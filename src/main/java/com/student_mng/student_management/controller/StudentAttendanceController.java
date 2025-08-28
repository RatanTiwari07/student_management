package com.student_mng.student_management.controller;

import com.student_mng.student_management.entity.Attendance;
import com.student_mng.student_management.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api/v1/student/attendance")
@RestController
@Tag(name = "Student Attendance", description = "Student attendance management and viewing operations")
@SecurityRequirement(name = "Bearer Authentication")
public class StudentAttendanceController implements BaseStudentController {
    
    private final StudentService studentService;

    public StudentAttendanceController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(
        summary = "Get student attendance records",
        description = "Retrieve attendance records for the authenticated student with optional filtering by subject and date range"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attendance records retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - Student role required")
    })
    @GetMapping
    public ResponseEntity<List<Attendance>> getAttendance(
            @Parameter(description = "Subject ID for filtering (optional)", example = "subject-12345")
            @RequestParam(required = false) String subjectId,
            @Parameter(description = "Start date for filtering (optional)", example = "2023-11-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for filtering (optional)", example = "2023-12-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (field,direction)", example = "date,desc")
            @RequestParam(defaultValue = "date,desc") String[] sort) {
        
        Pageable pageable = createPageable(page, size, sort);
        return ResponseEntity.ok(studentService.getFilteredAttendance(
            getCurrentUsername(), subjectId, startDate, endDate, pageable));
    }


    private Pageable createPageable(int page, int size, String[] sort) {
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sort[0]));
    }
}