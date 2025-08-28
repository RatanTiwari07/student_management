package com.student_mng.student_management.controller;

import com.student_mng.student_management.entity.ClassEntity;
import com.student_mng.student_management.entity.Student;
import com.student_mng.student_management.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/student/profile")
@RestController
@Tag(name = "Student Profile", description = "Student profile management and information retrieval")
@SecurityRequirement(name = "Bearer Authentication")
public class StudentProfileController implements BaseStudentController {
    
    private final StudentService studentService;

    public StudentProfileController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(
        summary = "Get student profile",
        description = "Retrieve the complete profile information of the authenticated student"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Student role required")
    })
    @GetMapping
    public ResponseEntity<Student> getStudentProfile() {
        return ResponseEntity.ok(studentService.getStudentByUsername(getCurrentUsername()));
    }

    @Operation(
        summary = "Get student's assigned class",
        description = "Retrieve the class information for the authenticated student"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student class retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ClassEntity.class))),
        @ApiResponse(responseCode = "404", description = "Student not assigned to any class"),
        @ApiResponse(responseCode = "403", description = "Access denied - Student role required")
    })
    @GetMapping("/class")
    public ResponseEntity<ClassEntity> getStudentClass() {
        return ResponseEntity.ok(studentService.getStudentClass(getCurrentUsername()));
    }
}