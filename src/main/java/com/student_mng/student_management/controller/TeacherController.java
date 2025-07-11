package com.student_mng.student_management.controller;

import com.student_mng.student_management.dto.AttendanceSubmissionDTO;
import com.student_mng.student_management.dto.AttendanceUpdateDTO;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.service.TeacherService;
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
    @GetMapping("/profile")
    public ResponseEntity<Teacher> getTeacherProfile() {
        return ResponseEntity.ok(teacherService.getTeacherByUsername(getCurrentUsername()));
    }

    @GetMapping("/classes")
    public ResponseEntity<List<ClassEntity>> getTeachingClasses() {
        return ResponseEntity.ok(teacherService.getTeacherClasses(getCurrentUsername()));
    }

    // Teaching assignments endpoints
    @GetMapping("/assignments")
    public ResponseEntity<List<TeacherAssignment>> getTeachingAssignments() {
        return ResponseEntity.ok(teacherService.getTeacherAssignments(getCurrentUsername()));
    }

//    @GetMapping("/class/{classId}/lectures")
//    public ResponseEntity<List<TeacherAssignment>> getClassLectures(@PathVariable String classId) {
//        return ResponseEntity.ok(teacherService.getTeacherAssignmentsByClass(getCurrentUsername(), classId));
//    }

    @GetMapping("/assigned-classes")
    public ResponseEntity<List<TeacherAssignment>> getAssignedClasses() {
        return ResponseEntity.ok(teacherService.getTeacherAssignments(getCurrentUsername()));
    }

    @GetMapping("/class/{classId}/students")
    public ResponseEntity<List<Student>> getClassStudents(
            @PathVariable String classId,
            @RequestParam String teacherAssignmentId,
            @RequestParam(required = false) LocalDate date) {

        // Verify teacher is authorized for this class and lecture
        if (!teacherService.isAuthorizedForTeacherAssignment(getCurrentUsername(), teacherAssignmentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LocalDate attendanceDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(teacherService.getStudentsForAttendance(getCurrentUsername(),classId, teacherAssignmentId ));
    }

    @PostMapping("/attendance/submit")
    public ResponseEntity<List<Attendance>> submitAttendance(
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

    // Optional: View past attendance records
    @GetMapping("/attendance/history")
    public ResponseEntity<Page<Attendance>> getAttendanceHistory(
            @RequestParam String teacherAssignmentId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
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

    @PutMapping("/attendance/{attendanceId}")
    public ResponseEntity<Attendance> updateAttendance(
            @PathVariable String attendanceId,
            @RequestBody AttendanceUpdateDTO updateDTO) {
        return ResponseEntity.ok(teacherService.updateAttendance(
            getCurrentUsername(),
            attendanceId,
            updateDTO.present()
        ));
    }
}
