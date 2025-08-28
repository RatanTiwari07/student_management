package com.student_mng.student_management.controller;

import com.student_mng.student_management.dto.*;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.service.AdminService;
import com.student_mng.student_management.service.BulkUploadService;
import com.student_mng.student_management.enums.FileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Management", description = "Administrative operations for managing the student management system")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private BulkUploadService bulkUploadService;

    // Subject Management
    @Operation(
        summary = "Create a new subject",
        description = "Add a new subject to the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject created successfully",
                    content = @Content(schema = @Schema(implementation = Subject.class))),
        @ApiResponse(responseCode = "400", description = "Invalid subject data"),
        @ApiResponse(responseCode = "409", description = "Subject already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(
            @Parameter(description = "Subject information", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = Subject.class),
                examples = @ExampleObject(value = """
                    {
                        "subjectName": "Mathematics"
                    }
                    """)))
            @RequestBody Subject subject) {
        return ResponseEntity.ok(adminService.registerSubject(subject));
    }

    @Operation(
        summary = "Get all subjects",
        description = "Retrieve a list of all subjects in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subjects retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Subject.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(adminService.getAllSubjects());
    }

    // User Management
    @Operation(
        summary = "Create a new admin user",
        description = "Register a new admin user in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin created successfully",
                    content = @Content(schema = @Schema(implementation = Admin.class))),
        @ApiResponse(responseCode = "400", description = "Invalid admin data"),
        @ApiResponse(responseCode = "409", description = "Email already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/users/admins")
    public ResponseEntity<Admin> createAdmin(
            @Parameter(description = "Admin user information", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = AdminDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "username": "admin_user",
                        "email": "admin@example.com",
                        "password": "securePassword123",
                        "dept": "Computer Science"
                    }
                    """)))
            @RequestBody AdminDTO adminDTO) {
        return ResponseEntity.ok(adminService.registerAdmin(adminDTO));
    }

    // Teacher Management
    @Operation(
        summary = "Create a new teacher",
        description = "Register a new teacher in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher created successfully",
                    content = @Content(schema = @Schema(implementation = Teacher.class))),
        @ApiResponse(responseCode = "400", description = "Invalid teacher data"),
        @ApiResponse(responseCode = "409", description = "Email already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/teachers")
    public ResponseEntity<Teacher> createTeacher(
            @Parameter(description = "Teacher information", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = TeacherDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "username": "teacher_john",
                        "email": "john@example.com",
                        "password": "teacherPass123"
                    }
                    """)))
            @RequestBody TeacherDTO teacherDTO) {
        return ResponseEntity.ok(adminService.registerTeacher(teacherDTO));
    }

    @Operation(
        summary = "Get all teachers",
        description = "Retrieve a list of all teachers in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teachers retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/teachers")
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(adminService.getAllTeachers());
    }

    @Operation(
        summary = "Get teacher by ID",
        description = "Retrieve a specific teacher by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher found",
                    content = @Content(schema = @Schema(implementation = Teacher.class))),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/teachers/{id}")
    public ResponseEntity<Teacher> getTeacherById(
            @Parameter(description = "Teacher ID", required = true, example = "12345-abcde-67890")
            @PathVariable String id) {
        return adminService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Assign teacher to class",
        description = "Create a teaching assignment for a teacher to a specific class and subject"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher assigned successfully",
                    content = @Content(schema = @Schema(implementation = TeacherAssignment.class))),
        @ApiResponse(responseCode = "400", description = "Invalid assignment data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/teachers/assignments")
    public ResponseEntity<TeacherAssignment> assignTeacherToClass(
            @Parameter(description = "Teacher assignment details", required = true)
            @RequestBody TeacherAssignment teacherAssignment) {
        return ResponseEntity.ok(adminService.assignTeacher(teacherAssignment));
    }

    // Class Management
    @Operation(
        summary = "Create a new class",
        description = "Add a new class to the system with optional subject assignments"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Class created successfully",
                    content = @Content(schema = @Schema(implementation = ClassEntity.class))),
        @ApiResponse(responseCode = "400", description = "Invalid class data"),
        @ApiResponse(responseCode = "409", description = "Class already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/classes")
    public ResponseEntity<ClassEntity> createClass(
            @Parameter(description = "Class information", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = ClassDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "className": "3rd Year - Section A",
                        "subjectIds": ["subject-id-1", "subject-id-2"]
                    }
                    """)))
            @RequestBody ClassDTO classDTO) {
        return ResponseEntity.ok(adminService.registerClass(classDTO));
    }

    @Operation(
        summary = "Get all classes",
        description = "Retrieve a list of all classes in the system with their subjects"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Classes retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/classes")
    public ResponseEntity<List<ClassResponseDTO>> getAllClasses() {
        List<ClassResponseDTO> classes = adminService.getAllClasses()
                .stream()
                .map(ClassResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(classes);
    }

    @Operation(
        summary = "Get class by ID",
        description = "Retrieve a specific class by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Class found",
                    content = @Content(schema = @Schema(implementation = ClassEntity.class))),
        @ApiResponse(responseCode = "404", description = "Class not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/classes/{id}")
    public ResponseEntity<ClassEntity> getClassById(
            @Parameter(description = "Class ID", required = true, example = "class-12345")
            @PathVariable String id) {
        return adminService.getClassById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Club Head Management
    @Operation(
        summary = "Create a new club head",
        description = "Register a new club head user in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Club head created successfully",
                    content = @Content(schema = @Schema(implementation = ClubHead.class))),
        @ApiResponse(responseCode = "400", description = "Invalid club head data"),
        @ApiResponse(responseCode = "409", description = "Email already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/club-heads")
    public ResponseEntity<ClubHead> createClubHead(
            @Parameter(description = "Club head information", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = ClubHeadDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "username": "club_head_user",
                        "email": "clubhead@example.com",
                        "password": "clubPass123"
                    }
                    """)))
            @RequestBody ClubHeadDTO clubHeadDTO) {
        return ResponseEntity.ok(adminService.registerClubHead(clubHeadDTO));
    }

    @Operation(
        summary = "Assign student as club head",
        description = "Promote an existing student to club head role for a specific club"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student assigned as club head successfully",
                    content = @Content(schema = @Schema(implementation = ClubHead.class))),
        @ApiResponse(responseCode = "400", description = "Invalid assignment data or student already a club head"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/students/{studentId}/assign-clubhead")
    public ResponseEntity<ClubHead> assignStudentAsClubHead(
            @Parameter(description = "Student ID", required = true, example = "student-12345")
            @PathVariable String studentId,
            @Parameter(description = "Club assignment details", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = AssignClubHeadDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "clubName": "Photography Club"
                    }
                    """)))
            @RequestBody AssignClubHeadDTO assignDTO) {
        // Override studentId from path parameter
        AssignClubHeadDTO dto = new AssignClubHeadDTO(studentId, assignDTO.clubName());
        return ResponseEntity.ok(adminService.assignStudentAsClubHead(dto));
    }

    @Operation(
        summary = "Remove club head status",
        description = "Remove club head role from a student"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Club head status removed successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found or not a club head"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @DeleteMapping("/students/{studentId}/remove-clubhead")
    public ResponseEntity<Void> removeClubHeadStatus(
            @Parameter(description = "Student ID", required = true, example = "student-12345")
            @PathVariable String studentId) {
        adminService.removeClubHeadStatus(studentId);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Update club head information",
        description = "Update club information for a student club head"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Club head information updated successfully",
                    content = @Content(schema = @Schema(implementation = ClubHead.class))),
        @ApiResponse(responseCode = "400", description = "Invalid update data"),
        @ApiResponse(responseCode = "404", description = "Student not found or not a club head"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PutMapping("/students/{studentId}/clubhead")
    public ResponseEntity<ClubHead> updateStudentClubHeadInfo(
            @Parameter(description = "Student ID", required = true, example = "student-12345")
            @PathVariable String studentId,
            @Parameter(description = "Updated club information", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = UpdateClubHeadDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "clubName": "Updated Photography Club"
                    }
                    """)))
            @RequestBody UpdateClubHeadDTO updateDTO) {
        return ResponseEntity.ok(adminService.updateStudentClubHeadInfo(studentId, updateDTO.clubName()));
    }

    @Operation(
        summary = "Get students who are club heads",
        description = "Retrieve all students who have been assigned club head roles"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Club head students retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/students/clubheads")
    public ResponseEntity<List<Student>> getStudentsWhoAreClubHeads() {
        return ResponseEntity.ok(adminService.getStudentsWhoAreClubHeads());
    }

    @Operation(
        summary = "Check if student is club head",
        description = "Check if a specific student has club head role"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed successfully",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/students/{studentId}/is-clubhead")
    public ResponseEntity<Boolean> isStudentClubHead(
            @Parameter(description = "Student ID", required = true, example = "student-12345")
            @PathVariable String studentId) {
        return ResponseEntity.ok(adminService.isStudentClubHead(studentId));
    }

    @Operation(
        summary = "Get all club heads",
        description = "Retrieve all club head users in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Club heads retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/club-heads")
    public ResponseEntity<List<ClubHead>> getAllClubHeads() {
        return ResponseEntity.ok(adminService.getAllClubHeads());
    }

    // Student Management
    @Operation(
        summary = "Create a new student",
        description = "Register a new student in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student created successfully",
                    content = @Content(schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "400", description = "Invalid student data"),
        @ApiResponse(responseCode = "409", description = "Email or roll number already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(
            @Parameter(description = "Student information", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = StudentDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "username": "john_doe",
                        "email": "john.doe@student.com",
                        "password": "studentPass123",
                        "rollNumber": "2023001",
                        "firstName": "John",
                        "lastName": "Doe",
                        "contactNumber": "1234567890",
                        "parentContactNumber": "9876543210",
                        "parentEmail": "parent@example.com",
                        "batch": "B1"
                    }
                    """)))
            @RequestBody StudentDTO studentDTO) {
        return ResponseEntity.ok(adminService.registerStudent(studentDTO));
    }

    @Operation(
        summary = "Get all students",
        description = "Retrieve a list of all students in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @Operation(
        summary = "Get unassigned students",
        description = "Retrieve students who are not assigned to any class"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unassigned students retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/students/unassigned")
    public ResponseEntity<List<Student>> getUnassignedStudents() {
        return ResponseEntity.ok(adminService.getUnassignedStudents());
    }

    @Operation(
        summary = "Get student by ID",
        description = "Retrieve a specific student by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student found",
                    content = @Content(schema = @Schema(implementation = Student.class))),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(
            @Parameter(description = "Student ID", required = true, example = "student-12345")
            @PathVariable String id) {
        return adminService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Assign students to class",
        description = "Assign multiple students to a specific class"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students assigned to class successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid assignment data"),
        @ApiResponse(responseCode = "404", description = "Class or students not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/students/class-assignments")
    public ResponseEntity<List<Student>> assignStudentsToClass(
            @Parameter(description = "Student assignment details", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = AssignStudentsDTO.class),
                examples = @ExampleObject(value = """
                    {
                        "studentIds": ["student-1", "student-2", "student-3"],
                        "classId": "class-12345"
                    }
                    """)))
            @RequestBody AssignStudentsDTO assignStudentsDTO) {
        return ResponseEntity.ok(adminService.assignStudentsToClass(
                assignStudentsDTO.studentIds(), assignStudentsDTO.classId()));
    }

    @Operation(
        summary = "Bulk upload students",
        description = "Upload multiple students from CSV or Excel file"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File processed successfully",
                    content = @Content(schema = @Schema(implementation = BulkUploadResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid file format or content"),
        @ApiResponse(responseCode = "413", description = "File size exceeds limit"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/students/bulk-upload")
    public ResponseEntity<BulkUploadResponse> bulkRegisterStudents(
            @Parameter(description = "CSV or Excel file containing student data", required = true)
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

    @Operation(
        summary = "Download bulk upload template",
        description = "Download a template file for bulk student upload"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Template downloaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid format specified"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/students/bulk-upload/template")
    public ResponseEntity<ByteArrayResource> downloadTemplate(
            @Parameter(description = "File format for template", example = "csv")
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
