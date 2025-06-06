package com.student_mng.student_management.service;

import com.student_mng.student_management.dto.AttendanceSubmissionDTO;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.enums.BatchType;
import com.student_mng.student_management.enums.LectureType;
import com.student_mng.student_management.exception.DuplicateAttendanceException;
import com.student_mng.student_management.exception.InvalidDateException;
import com.student_mng.student_management.exception.ResourceNotFoundException;
import com.student_mng.student_management.repository.AttendanceRepository;
import com.student_mng.student_management.repository.StudentRepository;
import com.student_mng.student_management.repository.TeacherAssignmentRepository;
import com.student_mng.student_management.repository.TeacherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    
    private final TeacherRepository teacherRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    public TeacherService(TeacherRepository teacherRepository,
                         TeacherAssignmentRepository teacherAssignmentRepository,
                         StudentRepository studentRepository,
                         AttendanceRepository attendanceRepository) {
        this.teacherRepository = teacherRepository;
        this.teacherAssignmentRepository = teacherAssignmentRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public Teacher getTeacherByUsername(String username) {
        return teacherRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
    }

    public List<TeacherAssignment> getTeacherAssignments(String username) {
        Teacher teacher = getTeacherByUsername(username);
        return teacherAssignmentRepository.findByTeacher(teacher);
    }

    public List<Student> getStudentsForAttendance(String username, String classId, String teacherAssignmentId) {
        // Verify teacher is authorized
        if (!isAuthorizedForTeacherAssignment(username, teacherAssignmentId)) {
            throw new ResourceNotFoundException("Teacher not authorized for this assignment");
        }

        TeacherAssignment assignment = teacherAssignmentRepository
            .findById(teacherAssignmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher Assignment not found"));

        // If it's a LAB lecture, return only students from the specific batch
        if (assignment.getLectureType() == LectureType.LAB) {
            return studentRepository.findByStudentClass_IdAndBatch(classId, assignment.getBatch());
        }

        // For theory lectures, return all students
        return studentRepository.findByStudentClass_Id(classId);
    }

    @Transactional
    public List<Attendance> submitAttendance(String username, String teacherAssignmentId, 
            LocalDate date, List<AttendanceSubmissionDTO.AttendanceRecord> records) {
        
        validateAttendanceDate(date);
        
        // Verify teacher is authorized
        if (!isAuthorizedForTeacherAssignment(username, teacherAssignmentId)) {
            throw new ResourceNotFoundException("Teacher not authorized for this assignment");
        }

        TeacherAssignment teacherAssignment = teacherAssignmentRepository
            .findById(teacherAssignmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher Assignment not found"));

        // Check for duplicate attendance
        boolean hasExistingAttendance = attendanceRepository
            .existsByTeacherAssignmentAndDate(teacherAssignment, date);
        if (hasExistingAttendance) {
            throw new DuplicateAttendanceException("Attendance already marked for this date and lecture");
        }

        List<Attendance> attendances = new ArrayList<>();
        
        // For LAB sessions, create two attendance entries
        if (teacherAssignment.getLectureType() == LectureType.LAB) {
            // First slot attendance
            List<Attendance> firstSlotAttendances = records.stream()
                .map(record -> createAttendance(teacherAssignment, record, date))
                .toList();
            
            // Second slot attendance (same records, different slot number)
            List<Attendance> secondSlotAttendances = records.stream()
                .map(record -> {
                    Attendance attendance = createAttendance(teacherAssignment, record, date);
                    attendance.setSlotNumber(2); // Indicate second slot
                    return attendance;
                })
                .toList();
            
            attendances.addAll(firstSlotAttendances);
            attendances.addAll(secondSlotAttendances);
        } else {
            // For THEORY lectures, create single attendance entry
            attendances = records.stream()
                .map(record -> createAttendance(teacherAssignment, record, date))
                .toList();
        }

        return attendanceRepository.saveAll(attendances);
    }

    private Attendance createAttendance(TeacherAssignment teacherAssignment, 
            AttendanceSubmissionDTO.AttendanceRecord record, LocalDate date) {
        
        Student student = studentRepository.findById(record.studentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        LectureSlot lectureSlot = teacherAssignment.getLectureSlot();
        BatchType batch = teacherAssignment.getBatch();

        return new Attendance(
            student, 
            teacherAssignment,
            lectureSlot,
            batch,
            date, 
            record.present()
        );
    }

    public boolean isAuthorizedForTeacherAssignment(String username, String teacherAssignmentId) {
        Teacher teacher = getTeacherByUsername(username);
        return teacherAssignmentRepository
            .findById(teacherAssignmentId)
            .map(ta -> ta.getTeacher().equals(teacher))
            .orElse(false);
    }

    public List<Attendance> getAttendanceHistory(String username, String teacherAssignmentId, 
            LocalDate startDate, LocalDate endDate) {
        // Verify teacher is authorized
        if (!isAuthorizedForTeacherAssignment(username, teacherAssignmentId)) {
            throw new ResourceNotFoundException("Teacher not authorized for this assignment");
        }

        TeacherAssignment assignment = teacherAssignmentRepository
            .findById(teacherAssignmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher Assignment not found"));

        // If dates are provided, filter by date range
        if (startDate != null && endDate != null) {
            return attendanceRepository.findByTeacherAssignmentAndDateBetweenOrderByDateDesc(
                assignment, startDate, endDate);
        }

        // Otherwise return all attendance records for this assignment
        return attendanceRepository.findByTeacherAssignmentOrderByDateDesc(assignment);
    }

    public Page<Attendance> getAttendanceHistoryPaginated(
            String username,
            String teacherAssignmentId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        
        // Verify teacher is authorized
        if (!isAuthorizedForTeacherAssignment(username, teacherAssignmentId)) {
            throw new ResourceNotFoundException("Teacher not authorized for this assignment");
        }

        TeacherAssignment assignment = teacherAssignmentRepository
            .findById(teacherAssignmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher Assignment not found"));

        // If dates are provided, filter by date range
        if (startDate != null && endDate != null) {
            return attendanceRepository.findByTeacherAssignmentAndDateBetween(
                assignment, startDate, endDate, pageable);
        }

        // Otherwise return all attendance records for this assignment
        return attendanceRepository.findByTeacherAssignment(assignment, pageable);
    }

    private void validateAttendanceDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        
        if (date.isAfter(today)) {
            throw new InvalidDateException("Cannot mark attendance for future dates");
        }
        
        if (date.isBefore(today.minusDays(7))) {
            throw new InvalidDateException("Cannot mark attendance for dates older than 7 days");
        }
    }

    @Transactional
    public Attendance updateAttendance(String username, String attendanceId, boolean present) {
        // Verify teacher is authorized
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));
        
        if (!isAuthorizedForTeacherAssignment(username, attendance.getTeacherAssignment().getId())) {
            throw new ResourceNotFoundException("Teacher not authorized for this attendance record");
        }
        
        // Validate date
        validateAttendanceDate(attendance.getDate());
        
        // Update attendance
        attendance.setPresent(present);
//        attendance.setLastModifiedAt(LocalDateTime.now());
//        attendance.setLastModifiedBy(username);
        
        return attendanceRepository.save(attendance);
    }

    public List<ClassEntity> getTeacherClasses(String username) {
        Teacher teacher = getTeacherByUsername(username);
        
        // Get all teacher assignments
        List<TeacherAssignment> assignments = teacherAssignmentRepository.findByTeacher(teacher);
        
        // Extract unique classes from assignments
        return assignments.stream()
            .map(TeacherAssignment::getAssignedClass)
            .distinct()
            .collect(Collectors.toList());
    }
}
