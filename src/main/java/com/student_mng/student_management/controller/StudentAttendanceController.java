package com.student_mng.student_management.controller;

import com.student_mng.student_management.entity.Attendance;
import com.student_mng.student_management.service.StudentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api/v1/student/attendance")
@RestController
public class StudentAttendanceController implements BaseStudentController {
    
    private final StudentService studentService;

    public StudentAttendanceController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getAttendance(
            @RequestParam(required = false) String subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date,desc") String[] sort) {
        
        Pageable pageable = createPageable(page, size, sort);
        return ResponseEntity.ok(studentService.getFilteredAttendance(
            getCurrentUsername(), subjectId, startDate, endDate, pageable));
    }

//    @GetMapping("/percentage")
//    public ResponseEntity<Map<String, Double>> getAllSubjectsAttendance() {
//        return ResponseEntity.ok(studentService.getAllSubjectsAttendance(getCurrentUsername()));
//    }
//
//    @GetMapping("/subject/{subjectId}")
//    public ResponseEntity<Double> getSubjectAttendance(@PathVariable String subjectId) {
//        return ResponseEntity.ok(studentService.getAttendancePercentage(getCurrentUsername(), subjectId));
//    }

    private Pageable createPageable(int page, int size, String[] sort) {
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sort[0]));
    }
}