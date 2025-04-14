package com.student_mng.student_management.controller;

import com.student_mng.student_management.dto.*;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/admin")
@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Subject Management
    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        return ResponseEntity.ok(adminService.registerSubject(subject));
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(adminService.getAllSubjects());
    }

    // User Management
    @PostMapping("/users/admins")
    public ResponseEntity<Admin> createAdmin(@RequestBody AdminDTO adminDTO) {
        return ResponseEntity.ok(adminService.registerAdmin(adminDTO));
    }

    // Teacher Management
    @PostMapping("/teachers")
    public ResponseEntity<Teacher> createTeacher(@RequestBody TeacherDTO teacherDTO) {
        return ResponseEntity.ok(adminService.registerTeacher(teacherDTO));
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(adminService.getAllTeachers());
    }

    @GetMapping("/teachers/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable String id) {
        return adminService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/teachers/assignments")
    public ResponseEntity<TeacherAssignment> assignTeacherToClass(@RequestBody TeacherAssignment teacherAssignment) {
        return ResponseEntity.ok(adminService.assignTeacher(teacherAssignment));
    }

    // Class Management
    @PostMapping("/classes")
    public ResponseEntity<ClassEntity> createClass(@RequestBody ClassEntity classEntity) {
        return ResponseEntity.ok(adminService.registerClass(classEntity));
    }

    @GetMapping("/classes")
    public ResponseEntity<List<ClassEntity>> getAllClasses() {
        return ResponseEntity.ok(adminService.getAllClasses());
    }

    @GetMapping("/classes/{id}")
    public ResponseEntity<ClassEntity> getClassById(@PathVariable String id) {
        return adminService.getClassById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Club Head Management
    @PostMapping("/club-heads")
    public ResponseEntity<ClubHead> createClubHead(@RequestBody ClubHeadDTO clubHeadDTO) {
        return ResponseEntity.ok(adminService.registerClubHead(clubHeadDTO));
    }

    @GetMapping("/club-heads")
    public ResponseEntity<List<ClubHead>> getAllClubHeads() {
        return ResponseEntity.ok(adminService.getAllClubHeads());
    }

    // Student Management
    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody StudentDTO studentDTO) {
        return ResponseEntity.ok(adminService.registerStudent(studentDTO));
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @GetMapping("/students/unassigned")
    public ResponseEntity<List<Student>> getUnassignedStudents() {
        return ResponseEntity.ok(adminService.getUnassignedStudents());
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable String id) {
        return adminService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/students/class-assignments")
    public ResponseEntity<List<Student>> assignStudentsToClass(@RequestBody AssignStudentsDTO assignStudentsDTO) {
        return ResponseEntity.ok(adminService.assignStudentsToClass(
                assignStudentsDTO.studentIds(), assignStudentsDTO.classId()));
    }
}

