package com.student_mng.student_management.service;

import com.student_mng.student_management.dto.AttendanceSubmissionDTO;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.enums.BatchType;
import com.student_mng.student_management.enums.LectureType;
import com.student_mng.student_management.enums.LectureSlotNumber;
import com.student_mng.student_management.exception.*;
import com.student_mng.student_management.repository.AttendanceRepository;
import com.student_mng.student_management.repository.StudentRepository;
import com.student_mng.student_management.repository.TeacherAssignmentRepository;
import com.student_mng.student_management.repository.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
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

    @Transactional(readOnly = true)
    public Teacher getTeacherByUsername(String username) {
        try {
            validateUsername(username);
            return teacherRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with username: " + username));
        } catch (DataAccessException e) {
            log.error("Database error while fetching teacher by username: {}", username, e);
            throw new DatabaseOperationException("Failed to fetch teacher", e);
        }
    }

    @Transactional(readOnly = true)
    public List<TeacherAssignment> getTeacherAssignments(String username) {
        try {
            Teacher teacher = getTeacherByUsername(username);
            return teacherAssignmentRepository.findByTeacher(teacher);
        } catch (DataAccessException e) {
            log.error("Database error while fetching teacher assignments for: {}", username, e);
            throw new DatabaseOperationException("Failed to fetch teacher assignments", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsForAttendance(String username, String classId, String teacherAssignmentId) {
        try {
            validateUsername(username);
            validateId(classId, "Class ID");
            validateId(teacherAssignmentId, "Teacher Assignment ID");

            // Verify teacher is authorized
            if (!isAuthorizedForTeacherAssignment(username, teacherAssignmentId)) {
                throw new UnauthorizedAccessException("Teacher not authorized for this assignment");
            }

            TeacherAssignment assignment = teacherAssignmentRepository
                .findById(teacherAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher Assignment not found"));

            // If it's a LAB lecture, return only students from the specific batch
            if (assignment.getLectureType() == LectureType.LAB) {
                List<Student> students = studentRepository.findByStudentClass_IdAndBatch(classId, assignment.getBatch());
                log.info("Found {} students for LAB assignment batch {}", students.size(), assignment.getBatch());
                return students;
            }

            // For theory lectures, return all students
            List<Student> students = studentRepository.findByStudentClass_Id(classId);
            log.info("Found {} students for THEORY assignment", students.size());
            return students;
        } catch (DataAccessException e) {
            log.error("Database error while fetching students for attendance: classId={}, assignmentId={}", classId, teacherAssignmentId, e);
            throw new DatabaseOperationException("Failed to fetch students for attendance", e);
        }
    }

    @Transactional
    public List<Attendance> submitAttendance(String username, String teacherAssignmentId, 
            LocalDate date, List<AttendanceSubmissionDTO.AttendanceRecord> records) {
        try {
            validateUsername(username);
            validateId(teacherAssignmentId, "Teacher Assignment ID");
            validateAttendanceDate(date);
            validateAttendanceRecords(records);

            // Verify teacher is authorized
            if (!isAuthorizedForTeacherAssignment(username, teacherAssignmentId)) {
                throw new UnauthorizedAccessException("Teacher not authorized for this assignment");
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
                log.info("Created attendance for {} students in LAB session (2 slots each)", records.size());
            } else {
                // For THEORY lectures, create single attendance entry
                attendances = records.stream()
                    .map(record -> createAttendance(teacherAssignment, record, date))
                    .toList();
                log.info("Created attendance for {} students in THEORY session", records.size());
            }

            return attendanceRepository.saveAll(attendances);
        } catch (DataAccessException e) {
            log.error("Database error while submitting attendance for assignment: {}", teacherAssignmentId, e);
            throw new DatabaseOperationException("Failed to submit attendance", e);
        }
    }

    private Attendance createAttendance(TeacherAssignment teacherAssignment, 
            AttendanceSubmissionDTO.AttendanceRecord record, LocalDate date) {
        try {
            Student student = studentRepository.findById(record.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + record.studentId()));

            DayOfWeek weekDay = teacherAssignment.getWeekDay();
            LectureSlotNumber lectureSlotNumber = teacherAssignment.getSlotNumber();
            BatchType batch = teacherAssignment.getBatch();

            return new Attendance(
                student,
                teacherAssignment,
                weekDay,
                lectureSlotNumber,
                batch,
                date,
                record.present()
            );
        } catch (Exception e) {
            log.error("Error creating attendance record for student: {}", record.studentId(), e);
            throw new BusinessLogicException("Failed to create attendance record for student: " + record.studentId(), e);
        }
    }

    @Transactional(readOnly = true)
    public boolean isAuthorizedForTeacherAssignment(String username, String teacherAssignmentId) {
        try {
            Teacher teacher = getTeacherByUsername(username);
            return teacherAssignmentRepository
                .findById(teacherAssignmentId)
                .map(ta -> ta.getTeacher().equals(teacher))
                .orElse(false);
        } catch (DataAccessException e) {
            log.error("Database error while checking teacher authorization: username={}, assignmentId={}", username, teacherAssignmentId, e);
            throw new DatabaseOperationException("Failed to verify teacher authorization", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceHistory(String username, String teacherAssignmentId,
            LocalDate startDate, LocalDate endDate) {
        try {
            validateUsername(username);
            validateId(teacherAssignmentId, "Teacher Assignment ID");

            // Verify teacher is authorized
            if (!isAuthorizedForTeacherAssignment(username, teacherAssignmentId)) {
                throw new UnauthorizedAccessException("Teacher not authorized for this assignment");
            }

            TeacherAssignment assignment = teacherAssignmentRepository
                .findById(teacherAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher Assignment not found"));

            // If dates are provided, filter by date range
            if (startDate != null && endDate != null) {
                validateDateRange(startDate, endDate);
                return attendanceRepository.findByTeacherAssignmentAndDateBetweenOrderByDateDesc(
                    assignment, startDate, endDate);
            }

            // Otherwise return all attendance records for this assignment
            return attendanceRepository.findByTeacherAssignmentOrderByDateDesc(assignment);
        } catch (DataAccessException e) {
            log.error("Database error while fetching attendance history: assignmentId={}", teacherAssignmentId, e);
            throw new DatabaseOperationException("Failed to fetch attendance history", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Attendance> getAttendanceHistoryPaginated(
            String username,
            String teacherAssignmentId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        try {
            validateUsername(username);
            validateId(teacherAssignmentId, "Teacher Assignment ID");
            validatePageable(pageable);

            // Verify teacher is authorized
            if (!isAuthorizedForTeacherAssignment(username, teacherAssignmentId)) {
                throw new UnauthorizedAccessException("Teacher not authorized for this assignment");
            }

            TeacherAssignment assignment = teacherAssignmentRepository
                .findById(teacherAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher Assignment not found"));

            // If dates are provided, filter by date range
            if (startDate != null && endDate != null) {
                validateDateRange(startDate, endDate);
                return attendanceRepository.findByTeacherAssignmentAndDateBetween(
                    assignment, startDate, endDate, pageable);
            }

            // Otherwise return all attendance records for this assignment
            return attendanceRepository.findByTeacherAssignment(assignment, pageable);
        } catch (DataAccessException e) {
            log.error("Database error while fetching paginated attendance history: assignmentId={}", teacherAssignmentId, e);
            throw new DatabaseOperationException("Failed to fetch attendance history", e);
        }
    }

    private void validateAttendanceDate(LocalDate date) {
        if (date == null) {
            throw new ValidationException("Attendance date cannot be null");
        }

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
        try {
            validateUsername(username);
            validateId(attendanceId, "Attendance ID");

            // Verify teacher is authorized
            Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));

            if (!isAuthorizedForTeacherAssignment(username, attendance.getTeacherAssignment().getId())) {
                throw new UnauthorizedAccessException("Teacher not authorized for this attendance record");
            }

            // Validate date
            validateAttendanceDate(attendance.getDate());

            // Update attendance
            attendance.setPresent(present);
            log.info("Updated attendance record {} to present: {}", attendanceId, present);

            return attendanceRepository.save(attendance);
        } catch (DataAccessException e) {
            log.error("Database error while updating attendance: {}", attendanceId, e);
            throw new DatabaseOperationException("Failed to update attendance", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ClassEntity> getTeacherClasses(String username) {
        try {
            Teacher teacher = getTeacherByUsername(username);

            // Get all teacher assignments
            List<TeacherAssignment> assignments = teacherAssignmentRepository.findByTeacher(teacher);

            // Extract unique classes from assignments
            return assignments.stream()
                .map(TeacherAssignment::getAssignedClass)
                .distinct()
                .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Database error while fetching teacher classes: {}", username, e);
            throw new DatabaseOperationException("Failed to fetch teacher classes", e);
        }
    }

    // Validation methods
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
    }

    private void validateId(String id, String fieldName) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    private void validateAttendanceRecords(List<AttendanceSubmissionDTO.AttendanceRecord> records) {
        if (records == null || records.isEmpty()) {
            throw new ValidationException("Attendance records cannot be empty");
        }

        for (AttendanceSubmissionDTO.AttendanceRecord record : records) {
            if (record.studentId() == null || record.studentId().trim().isEmpty()) {
                throw new ValidationException("Student ID cannot be empty in attendance record");
            }
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date");
        }

        if (startDate.isBefore(LocalDate.now().minusYears(1))) {
            throw new ValidationException("Start date cannot be more than 1 year in the past");
        }
    }

    private void validatePageable(Pageable pageable) {
        if (pageable == null) {
            throw new ValidationException("Pageable cannot be null");
        }

        if (pageable.getPageSize() > 100) {
            throw new ValidationException("Page size cannot exceed 100 records");
        }
    }
}
