package com.student_mng.student_management.controller;

import com.student_mng.student_management.dto.AttendanceSubmissionDTO;
import com.student_mng.student_management.dto.AttendanceUpdateDTO;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/api/v1/teacher")
@RestController
@Tag(name = "Teacher Management", description = "Teacher operations for managing classes, assignments, and attendance")
@SecurityRequirement(name = "Bearer Authentication")
public class TeacherController{
    
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    // Profile endpoints
    @Operation(
        summary = "Get teacher profile",
        description = "Retrieve the profile information of the authenticated teacher"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Teacher.class))),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Teacher role required")
    })
    @GetMapping("/profile")
    public ResponseEntity<Teacher> getTeacherProfile() {
        return ResponseEntity.ok(teacherService.getTeacherByUsername(getCurrentUsername()));
    }

    @Operation(
        summary = "Get teaching classes",
        description = "Retrieve all classes that the authenticated teacher is assigned to"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teaching classes retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Teacher role required")
    })
    @GetMapping("/classes")
    public ResponseEntity<List<ClassEntity>> getTeachingClasses() {
        return ResponseEntity.ok(teacherService.getTeacherClasses(getCurrentUsername()));
    }

    // Teaching assignments endpoints
    @Operation(
        summary = "Get teaching assignments",
        description = "Retrieve all teaching assignments for the authenticated teacher"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teaching assignments retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Teacher role required")
    })
    @GetMapping("/assignments")
    public ResponseEntity<List<TeacherAssignment>> getTeachingAssignments() {
        return ResponseEntity.ok(teacherService.getTeacherAssignments(getCurrentUsername()));
    }

    @Operation(
        summary = "Get assigned classes",
        description = "Retrieve all class assignments for the authenticated teacher"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assigned classes retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Teacher role required")
    })
    @GetMapping("/assigned-classes")
    public ResponseEntity<List<TeacherAssignment>> getAssignedClasses() {
        return ResponseEntity.ok(teacherService.getTeacherAssignments(getCurrentUsername()));
    }

    @Operation(
        summary = "Get class students for attendance",
        description = "Retrieve students from a specific class for attendance marking"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Class students retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Teacher not authorized for this assignment"),
        @ApiResponse(responseCode = "404", description = "Class or assignment not found")
    })
    @GetMapping("/class/{classId}/students")
    public ResponseEntity<List<Student>> getClassStudents(
            @Parameter(description = "Class ID", required = true, example = "class-12345")
            @PathVariable String classId,
            @Parameter(description = "Teacher Assignment ID", required = true, example = "assignment-12345")
            @RequestParam String teacherAssignmentId,
            @Parameter(description = "Date for attendance (optional, defaults to today)", example = "2023-12-01")
            @RequestParam(required = false) LocalDate date) {

        // Verify teacher is authorized for this class and lecture
        if (!teacherService.isAuthorizedForTeacherAssignment(getCurrentUsername(), teacherAssignmentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LocalDate attendanceDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(teacherService.getStudentsForAttendance(getCurrentUsername(),classId, teacherAssignmentId ));
    }

    @Operation(
        summary = "Submit attendance",
        description = "Submit attendance records for students in a class"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attendance submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid attendance data or duplicate submission"),
        @ApiResponse(responseCode = "403", description = "Access denied - Teacher not authorized for this assignment"),
        @ApiResponse(responseCode = "409", description = "Attendance already marked for this date")
    })
    @PostMapping("/attendance/submit")
    public ResponseEntity<List<Attendance>> submitAttendance(
            @Parameter(description = "Attendance submission details", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = AttendanceSubmissionDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "teacherAssignmentId": "assignment-12345",
                        "date": "2023-12-01",
                        "attendanceRecords": [
                            {
                                "studentId": "student-1",
                                "present": true
                            },
                            {
                                "studentId": "student-2",
                                "present": false
                            }
                        ]
                    }
                    """)))
            @RequestBody AttendanceSubmissionDTO submission) {

        // Verify teacher is authorized for this lecture
        if (!teacherService.isAuthorizedForTeacherAssignment(getCurrentUsername(),
                submission.teacherAssignmentId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(teacherService.submitAttendance(
                getCurrentUsername(),
                submission.teacherAssignmentId(),
                submission.date(),
                submission.attendanceRecords()
        ));
    }

    @Operation(
        summary = "Get attendance history",
        description = "Retrieve paginated attendance history for a specific teaching assignment"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attendance history retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Teacher not authorized for this assignment"),
        @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @GetMapping("/attendance/history")
    public ResponseEntity<Page<Attendance>> getAttendanceHistory(
            @Parameter(description = "Teacher Assignment ID", required = true, example = "assignment-12345")
            @RequestParam String teacherAssignmentId,
            @Parameter(description = "Start date for filtering (optional)", example = "2023-11-01")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "End date for filtering (optional)", example = "2023-12-01")
            @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort criteria (field,direction)", example = "date,desc")
            @RequestParam(defaultValue = "date,desc") String[] sort) {
        
        Pageable pageable = PageRequest.of(page, size, 
            Sort.by(Arrays.stream(sort)
                .map(this::createOrder)
                .toList()));
        
        return ResponseEntity.ok(teacherService.getAttendanceHistoryPaginated(
            getCurrentUsername(),
            teacherAssignmentId,
            startDate,
            endDate,
            pageable
        ));
    }

    private Sort.Order createOrder(String sort) {
        String[] parts = sort.split(",");
        return new Sort.Order(
            parts.length > 1 && parts[1].equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            parts[0]
        );
    }

    @Operation(
        summary = "Update attendance record",
        description = "Update a specific attendance record"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attendance updated successfully",
                    content = @Content(schema = @Schema(implementation = Attendance.class))),
        @ApiResponse(responseCode = "400", description = "Invalid update data or date restrictions"),
        @ApiResponse(responseCode = "403", description = "Access denied - Teacher not authorized for this record"),
        @ApiResponse(responseCode = "404", description = "Attendance record not found")
    })
    @PutMapping("/attendance/{attendanceId}")
    public ResponseEntity<Attendance> updateAttendance(
            @Parameter(description = "Attendance record ID", required = true, example = "attendance-12345")
            @PathVariable String attendanceId,
            @Parameter(description = "Updated attendance data", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = AttendanceUpdateDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "present": true
                    }
                    """)))
            @RequestBody AttendanceUpdateDTO updateDTO) {
        return ResponseEntity.ok(teacherService.updateAttendance(
            getCurrentUsername(),
            attendanceId,
            updateDTO.present()
        ));
    }
}
