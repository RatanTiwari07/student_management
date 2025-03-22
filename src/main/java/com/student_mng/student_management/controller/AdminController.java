package com.student_mng.student_management.controller;

import com.student_mng.student_management.dto.*;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/admin")
@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/subject")
    public ResponseEntity<Subject> registerSubject(@RequestBody Subject subject) {
        return ResponseEntity.ok(adminService.registerSubject(subject));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerAdmin(@RequestBody AdminDTO adminDTO) {
        return ResponseEntity.ok(adminService.registerAdmin(adminDTO));
    }

    @PostMapping("/teacher")
    public ResponseEntity<Teacher> registerTeacher(@RequestBody TeacherDTO teacherDTO) {
        return ResponseEntity.ok(adminService.registerTeacher(teacherDTO));
    }

    @PostMapping("/assign-teacher")
    public ResponseEntity<TeacherAssignment> assignTeacher(@RequestBody TeacherAssignment teacherAssignment) {
        return ResponseEntity.ok(adminService.assignTeacher(teacherAssignment));
    }

    @PostMapping("/class")
    public ResponseEntity<ClassEntity> registerClass(@RequestBody ClassEntity classEntity) {
        return ResponseEntity.ok(adminService.registerClass(classEntity));
    }

    @PostMapping("/clubhead")
    public ResponseEntity<ClubHead> registerClubHead(@RequestBody ClubHeadDTO clubHeadDTO) {
        return ResponseEntity.ok(adminService.registerClubHead(clubHeadDTO));
    }

    @PostMapping("/student")
    public ResponseEntity<Student> registerStudent(@RequestBody StudentDTO studentDTO) {
        return ResponseEntity.ok(adminService.registerStudent(studentDTO));
    }

    @PostMapping("/assign-students")
    public ResponseEntity<List<Student>> assignStudentsToClass(
            @RequestBody AssignStudentsDTO assignStudentsDTO) {
        return ResponseEntity.ok(adminService.assignStudentsToClass(
                assignStudentsDTO.studentIds(), assignStudentsDTO.classId()));
    }
}

