package com.student_mng.student_management.controller;

import com.student_mng.student_management.dto.*;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.service.AdminService;
import com.student_mng.student_management.service.BulkUploadService;
import com.student_mng.student_management.enums.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/v1/admin")
@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private BulkUploadService bulkUploadService;

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

    @PostMapping("/students/bulk-upload")
    public ResponseEntity<BulkUploadResponse> bulkRegisterStudents(
            @RequestParam("file") MultipartFile file) {
        try {
            String extension = getFileExtension(file.getOriginalFilename());
            FileType fileType = FileType.fromExtension(extension);
            
            BulkUploadResponse response = bulkUploadService.processFile(
                file, 
                fileType,
                this::registerStudent
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new BulkUploadResponse(0, 0, List.of(), 
                        List.of("Invalid file format. Only CSV and Excel (xlsx) files are supported")));
        }
    }

    @GetMapping("/students/bulk-upload/template")
    public ResponseEntity<ByteArrayResource> downloadTemplate(
            @RequestParam(defaultValue = "csv") String format) {
        try {
            FileType fileType = FileType.fromExtension(format);
            ByteArrayResource resource = bulkUploadService.generateTemplate(fileType);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileType.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=student-template." + fileType.getExtension())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private void registerStudent(StudentDTO studentDTO) {
        adminService.registerStudent(studentDTO);
    }

    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1).toLowerCase())
                .orElse("");
    }
}

