package com.student_mng.student_management.controller;

import com.student_mng.student_management.entity.Event;
import com.student_mng.student_management.entity.Student;
import com.student_mng.student_management.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1/student/events")
@RestController
public class StudentEventController implements BaseStudentController {
    
    private final StudentService studentService;

    public StudentEventController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/all")
    public ResponseEntity<Set<Event>> getAllEvents() {
        return ResponseEntity.ok(studentService.getAllEvents());
    }

    @GetMapping("/registered")
    public ResponseEntity<Set<Event>> getRegisteredEvents() {
        return ResponseEntity.ok(studentService.getRegisteredEvents(getCurrentUsername()));
    }

    @PostMapping("/{eventId}/register")
    public ResponseEntity<Student> registerForEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(studentService.registerForEvent(getCurrentUsername(), eventId));
    }

    @DeleteMapping("/{eventId}/unregister")
    public ResponseEntity<Student> unregisterFromEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(studentService.unregisterFromEvent(getCurrentUsername(), eventId));
    }
}