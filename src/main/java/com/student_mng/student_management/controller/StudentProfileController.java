package com.student_mng.student_management.controller;

import com.student_mng.student_management.entity.ClassEntity;
import com.student_mng.student_management.entity.Student;
import com.student_mng.student_management.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/student/profile")
@RestController
public class StudentProfileController implements BaseStudentController {
    
    private final StudentService studentService;

    public StudentProfileController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<Student> getStudentProfile() {
        return ResponseEntity.ok(studentService.getStudentByUsername(getCurrentUsername()));
    }

    @GetMapping("/class")
    public ResponseEntity<ClassEntity> getStudentClass() {
        return ResponseEntity.ok(studentService.getStudentClass(getCurrentUsername()));
    }
}