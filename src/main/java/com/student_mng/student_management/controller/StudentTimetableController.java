package com.student_mng.student_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/student/timetable")
@RestController
public class StudentTimetableController implements BaseStudentController {
    
//    private final StudentService studentService;
//
//    public StudentTimetableController(StudentService studentService) {
//        this.studentService = studentService;
//    }

//    @GetMapping
//    public ResponseEntity<List<TeacherAssignment>> getStudentTimetable() {
//        return ResponseEntity.ok(studentService.getStudentTimetable(getCurrentUsername()));
//    }
//
//    @GetMapping("/by-day")
//    public ResponseEntity<Map<DayOfWeek, List<TeacherAssignment>>> getStudentTimetableByDay() {
//        return ResponseEntity.ok(studentService.getStudentTimetableByDay(getCurrentUsername()));
//    }
}