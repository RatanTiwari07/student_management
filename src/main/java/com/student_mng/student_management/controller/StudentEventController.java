package com.student_mng.student_management.controller;

import com.student_mng.student_management.entity.Event;
import com.student_mng.student_management.entity.Student;
import com.student_mng.student_management.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1/student/events")
@RestController
@Tag(name = "Student Events", description = "Student event registration and management operations")
@SecurityRequirement(name = "Bearer Authentication")
public class StudentEventController implements BaseStudentController {
    
    private final StudentService studentService;

    public StudentEventController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(
        summary = "Get all available events",
        description = "Retrieve all events available for student registration"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Events retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Event.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - Student role required")
    })
    @GetMapping("/all")
    public ResponseEntity<Set<Event>> getAllEvents() {
        return ResponseEntity.ok(studentService.getAllEvents());
    }

    @Operation(
        summary = "Get registered events",
        description = "Retrieve all events the authenticated student is registered for"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registered events retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Event.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - Student role required")
    })
    @GetMapping("/registered")
    public ResponseEntity<Set<Event>> getRegisteredEvents() {
        return ResponseEntity.ok(studentService.getRegisteredEvents(getCurrentUsername()));
    }

    @Operation(
        summary = "Register for an event",
        description = "Register the authenticated student for a specific event"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully registered for event",
                    content = @Content(schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "400", description = "Student already registered for this event"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Student role required")
    })
    @PostMapping("/{eventId}/register")
    public ResponseEntity<Student> registerForEvent(
            @Parameter(description = "Event ID", required = true, example = "event-12345")
            @PathVariable String eventId) {
        return ResponseEntity.ok(studentService.registerForEvent(getCurrentUsername(), eventId));
    }

    @Operation(
        summary = "Unregister from an event",
        description = "Unregister the authenticated student from a specific event"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully unregistered from event",
                    content = @Content(schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "400", description = "Student not registered for this event"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Student role required")
    })
    @DeleteMapping("/{eventId}/unregister")
    public ResponseEntity<Student> unregisterFromEvent(
            @Parameter(description = "Event ID", required = true, example = "event-12345")
            @PathVariable String eventId) {
        return ResponseEntity.ok(studentService.unregisterFromEvent(getCurrentUsername(), eventId));
    }
}