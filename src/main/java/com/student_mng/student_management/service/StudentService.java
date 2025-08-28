package com.student_mng.student_management.service;

import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.exception.*;
import com.student_mng.student_management.repository.AttendanceRepository;
import com.student_mng.student_management.repository.EventRepository;
import com.student_mng.student_management.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;

    public StudentService(StudentRepository studentRepository, AttendanceRepository attendanceRepository, EventRepository eventRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public Student getStudentByUsername(String username) {
        try {
            validateUsername(username);
            return studentRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with username: " + username));
        } catch (DataAccessException e) {
            log.error("Database error while fetching student by username: {}", username, e);
            throw new DatabaseOperationException("Failed to fetch student", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Attendance> getFilteredAttendance(String username, String subjectId,
            LocalDate startDate, LocalDate endDate, Pageable pageable) {
        try {
            validateUsername(username);
            validatePageable(pageable);

            if (startDate != null && endDate != null) {
                validateDateRange(startDate, endDate);
            }

            Student student = getStudentByUsername(username);

            if (subjectId != null && startDate != null && endDate != null) {
                validateId(subjectId, "Subject ID");
                return attendanceRepository
                        .findByStudentAndTeacherAssignment_Subject_IdAndDateBetweenOrderByDateDesc(
                                student, subjectId, startDate, endDate);
            } else if (subjectId != null) {
                validateId(subjectId, "Subject ID");
                return attendanceRepository
                        .findByStudentAndTeacherAssignment_Subject_IdOrderByDateDesc(
                                student, subjectId);
            } else if (startDate != null && endDate != null) {
                return attendanceRepository
                        .findByStudentAndDateBetweenOrderByDateDesc(student, startDate, endDate);
            } else {
                return attendanceRepository.findByStudentOrderByDateDesc(student);
            }
        } catch (DataAccessException e) {
            log.error("Database error while fetching filtered attendance for student: {}", username, e);
            throw new DatabaseOperationException("Failed to fetch attendance records", e);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Double> getAllSubjectsAttendance(String username) {
        try {
            validateUsername(username);
            Student student = getStudentByUsername(username);
            Map<String, Double> attendancePercentages = new HashMap<>();

            // Get all subjects for the student's class
            if (student.getStudentClass() == null) {
                throw new BusinessLogicException("Student is not assigned to any class");
            }

            List<Subject> subjects = student.getStudentClass().getSubjects();
            if (subjects == null || subjects.isEmpty()) {
                log.warn("No subjects found for student's class: {}", student.getStudentClass().getClassName());
                return attendancePercentages;
            }

            for (Subject subject : subjects) {
                try {
                    Double percentage = calculateAttendancePercentage(student, subject.getId());
                    attendancePercentages.put(subject.getSubjectName(), percentage);
                } catch (Exception e) {
                    log.warn("Error calculating attendance for subject {}: {}", subject.getSubjectName(), e.getMessage());
                    attendancePercentages.put(subject.getSubjectName(), 0.0);
                }
            }

            return attendancePercentages;
        } catch (DataAccessException e) {
            log.error("Database error while fetching all subjects attendance for student: {}", username, e);
            throw new DatabaseOperationException("Failed to fetch attendance summary", e);
        }
    }

    @Transactional(readOnly = true)
    public Double getAttendancePercentage(String username, String subjectId) {
        try {
            validateUsername(username);
            validateId(subjectId, "Subject ID");
            Student student = getStudentByUsername(username);
            return calculateAttendancePercentage(student, subjectId);
        } catch (DataAccessException e) {
            log.error("Database error while calculating attendance percentage for student: {} and subject: {}", username, subjectId, e);
            throw new DatabaseOperationException("Failed to calculate attendance percentage", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsBelowAttendanceThreshold(String subjectId, Double threshold) {
        try {
            validateId(subjectId, "Subject ID");
            validateAttendanceThreshold(threshold);
            return attendanceRepository.findStudentsBelowAttendanceThreshold(subjectId, threshold);
        } catch (DataAccessException e) {
            log.error("Database error while fetching students below attendance threshold for subject: {}", subjectId, e);
            throw new DatabaseOperationException("Failed to fetch students below attendance threshold", e);
        }
    }

    private Double calculateAttendancePercentage(Student student, String subjectId) {
        try {
            // Get all attendance records for the student and subject
            List<Attendance> attendances = attendanceRepository.findByStudentAndTeacherAssignment_Subject_Id(
                student, subjectId);

            if (attendances.isEmpty()) {
                return 0.0;
            }

            // Group attendances by date and teacher assignment to handle lab sessions
            Map<String, List<Attendance>> groupedAttendances = attendances.stream()
                .collect(Collectors.groupingBy(attendance ->
                    attendance.getDate().toString() + "_" + attendance.getTeacherAssignment().getId()));

            long totalPresent = 0;
            long totalSessions = groupedAttendances.size(); // Each group represents one session (lab or theory)

            // Calculate present sessions
            for (List<Attendance> sessionAttendances : groupedAttendances.values()) {
                // For lab sessions (will have 2 records), student needs to be present in both slots
                if (sessionAttendances.size() > 1) {
                    boolean presentInAllSlots = sessionAttendances.stream()
                        .allMatch(Attendance::isPresent);
                    if (presentInAllSlots) {
                        totalPresent++;
                    }
                } else {
                    // For theory sessions (single record)
                    if (sessionAttendances.get(0).isPresent()) {
                        totalPresent++;
                    }
                }
            }

            return totalSessions == 0 ? 0.0 : (double) (totalPresent * 100) / totalSessions;
        } catch (Exception e) {
            log.error("Error calculating attendance percentage for student: {} and subject: {}", student.getUsername(), subjectId, e);
            throw new BusinessLogicException("Failed to calculate attendance percentage", e);
        }
    }

    @Transactional
    public void updateStudentProfile(String username, Student updatedStudent) {
        try {
            validateUsername(username);
            validateStudentProfileData(updatedStudent);

            Student student = getStudentByUsername(username);

            // Update only allowed fields
            student.setFirstName(updatedStudent.getFirstName());
            student.setLastName(updatedStudent.getLastName());
            student.setContactNumber(updatedStudent.getContactNumber());
            student.setParentContactNumber(updatedStudent.getParentContactNumber());
            student.setParentEmail(updatedStudent.getParentEmail());

            studentRepository.save(student);
            log.info("Updated profile for student: {}", username);
        } catch (DataAccessException e) {
            log.error("Database error while updating student profile: {}", username, e);
            throw new DatabaseOperationException("Failed to update student profile", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Student> searchStudents(String query) {
        try {
            validateSearchQuery(query);
            return studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrRollNumberContainingIgnoreCase(
                    query, query, query);
        } catch (DataAccessException e) {
            log.error("Database error while searching students with query: {}", query, e);
            throw new DatabaseOperationException("Failed to search students", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsByClass(String classId) {
        try {
            validateId(classId, "Class ID");
            return studentRepository.findByStudentClass_Id(classId);
        } catch (DataAccessException e) {
            log.error("Database error while fetching students by class: {}", classId, e);
            throw new DatabaseOperationException("Failed to fetch students by class", e);
        }
    }

    @Transactional(readOnly = true)
    public ClassEntity getStudentClass(String username) {
        try {
            validateUsername(username);
            Student student = getStudentByUsername(username);
            if (student.getStudentClass() == null) {
                throw new ResourceNotFoundException("No class assigned to student with username: " + username);
            }
            return student.getStudentClass();
        } catch (DataAccessException e) {
            log.error("Database error while fetching student class: {}", username, e);
            throw new DatabaseOperationException("Failed to fetch student class", e);
        }
    }

    @Transactional(readOnly = true)
    public Set<Event> getAllEvents() {
        try {
            return new HashSet<>(eventRepository.findAll());
        } catch (DataAccessException e) {
            log.error("Database error while fetching all events", e);
            throw new DatabaseOperationException("Failed to fetch events", e);
        }
    }

    @Transactional(readOnly = true)
    public Set<Event> getRegisteredEvents(String username) {
        try {
            validateUsername(username);
            Student student = getStudentByUsername(username);
            Set<Event> events = student.getRegisteredEvents();
            return events != null ? events : new HashSet<>();
        } catch (DataAccessException e) {
            log.error("Database error while fetching registered events for student: {}", username, e);
            throw new DatabaseOperationException("Failed to fetch registered events", e);
        }
    }

    @Transactional
    public Student registerForEvent(String username, String eventId) {
        try {
            validateUsername(username);
            validateId(eventId, "Event ID");

            Student student = getStudentByUsername(username);
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

            // Check if already registered
            if (student.getRegisteredEvents().contains(event)) {
                throw new BusinessLogicException("Student is already registered for this event");
            }

            student.getRegisteredEvents().add(event);
            Student savedStudent = studentRepository.save(student);
            log.info("Student {} registered for event: {}", username, event.getName());
            return savedStudent;
        } catch (DataAccessException e) {
            log.error("Database error while registering student for event: student={}, event={}", username, eventId, e);
            throw new DatabaseOperationException("Failed to register for event", e);
        }
    }

    @Transactional
    public Student unregisterFromEvent(String username, String eventId) {
        try {
            validateUsername(username);
            validateId(eventId, "Event ID");

            Student student = getStudentByUsername(username);
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

            // Check if actually registered
            if (!student.getRegisteredEvents().contains(event)) {
                throw new BusinessLogicException("Student is not registered for this event");
            }

            student.getRegisteredEvents().remove(event);
            Student savedStudent = studentRepository.save(student);
            log.info("Student {} unregistered from event: {}", username, event.getName());
            return savedStudent;
        } catch (DataAccessException e) {
            log.error("Database error while unregistering student from event: student={}, event={}", username, eventId, e);
            throw new DatabaseOperationException("Failed to unregister from event", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAttendanceSummary(String username) {
        try {
            validateUsername(username);
            Student student = getStudentByUsername(username);
            // Additional logic for attendance summary would go here
            // This is just a placeholder as the specific requirements weren't shown
            return List.of();
        } catch (DataAccessException e) {
            log.error("Database error while fetching attendance summary for student: {}", username, e);
            throw new DatabaseOperationException("Failed to fetch attendance summary", e);
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

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date");
        }

        if (startDate.isBefore(LocalDate.now().minusYears(2))) {
            throw new ValidationException("Start date cannot be more than 2 years in the past");
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

    private void validateAttendanceThreshold(Double threshold) {
        if (threshold == null) {
            throw new ValidationException("Attendance threshold cannot be null");
        }

        if (threshold < 0 || threshold > 100) {
            throw new ValidationException("Attendance threshold must be between 0 and 100");
        }
    }

    private void validateStudentProfileData(Student student) {
        if (student == null) {
            throw new ValidationException("Student data cannot be null");
        }

        if (student.getFirstName() != null && student.getFirstName().trim().isEmpty()) {
            throw new ValidationException("First name cannot be empty");
        }

        if (student.getLastName() != null && student.getLastName().trim().isEmpty()) {
            throw new ValidationException("Last name cannot be empty");
        }

        // Validate email format if provided
        if (student.getParentEmail() != null && !student.getParentEmail().trim().isEmpty()) {
            if (!isValidEmail(student.getParentEmail())) {
                throw new ValidationException("Invalid parent email format");
            }
        }
    }

    private void validateSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ValidationException("Search query cannot be empty");
        }

        if (query.length() < 2) {
            throw new ValidationException("Search query must be at least 2 characters long");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}