package com.student_mng.student_management.service;

import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.exception.ResourceNotFoundException;
import com.student_mng.student_management.repository.AttendanceRepository;
import com.student_mng.student_management.repository.EventRepository;
import com.student_mng.student_management.repository.StudentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
//@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;

    public StudentService(StudentRepository studentRepository, AttendanceRepository attendanceRepository, EventRepository eventRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.eventRepository = eventRepository;
    }

    public Student getStudentByUsername(String username) {
        return studentRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with username: " + username));
    }

    public List<Attendance> getFilteredAttendance(String username, String subjectId, 
            LocalDate startDate, LocalDate endDate, Pageable pageable) {
        
        Student student = getStudentByUsername(username);
        
        if (subjectId != null && startDate != null && endDate != null) {
            return attendanceRepository
                    .findByStudentAndTeacherAssignment_Subject_IdAndDateBetweenOrderByDateDesc(
                            student, subjectId, startDate, endDate);
        } else if (subjectId != null) {
            return attendanceRepository
                    .findByStudentAndTeacherAssignment_Subject_IdOrderByDateDesc(
                            student, subjectId);
        } else if (startDate != null && endDate != null) {
            return attendanceRepository
                    .findByStudentAndDateBetweenOrderByDateDesc(student, startDate, endDate);
        } else {
            return attendanceRepository.findByStudentOrderByDateDesc(student);
        }
    }

//    public Map<String, Double> getAllSubjectsAttendance(String username) {
//        Student student = getStudentByUsername(username);
//        Map<String, Double> attendancePercentages = new HashMap<>();
//
//        // Get all subjects for the student's class
//        List<Subject> subjects = student.getStudentClass().getSubjects();
//
//        for (Subject subject : subjects) {
//            Double percentage = calculateAttendancePercentage(student, subject.getId());
//            attendancePercentages.put(subject.getSubjectName(), percentage);
//        }
//
//        return attendancePercentages;
//    }

//    public Double getAttendancePercentage(String username, String subjectId) {
//        Student student = getStudentByUsername(username);
//        return calculateAttendancePercentage(student, subjectId);
//    }

//    private Double calculateAttendancePercentage(Student student, String subjectId) {
//        Long presentCount = attendanceRepository.countPresentAttendances(student, subjectId);
//        Long totalCount = attendanceRepository.countTotalAttendances(student, subjectId);
//
//        if (totalCount == 0) {
//            return 0.0;
//        }
//
//        return (double) (presentCount * 100) / totalCount;
//    }

    public List<Student> getStudentsBelowAttendanceThreshold(String subjectId, Double threshold) {
        return attendanceRepository.findStudentsBelowAttendanceThreshold(subjectId, threshold);
    }

    private Double calculateAttendancePercentage(Student student, String subjectId) {
        Long presentCount = attendanceRepository.countPresentAttendances(student, subjectId);
        Long totalCount = attendanceRepository.countTotalAttendances(student, subjectId);

        if (totalCount == 0) {
            return 0.0;
        }

        return (double) (presentCount * 100) / totalCount;
    }

    @Transactional
    public void updateStudentProfile(String username, Student updatedStudent) {
        Student student = getStudentByUsername(username);
        
        // Update only allowed fields
        student.setFirstName(updatedStudent.getFirstName());
        student.setLastName(updatedStudent.getLastName());
        student.setContactNumber(updatedStudent.getContactNumber());
        student.setParentContactNumber(updatedStudent.getParentContactNumber());
        student.setParentEmail(updatedStudent.getParentEmail());
        
        studentRepository.save(student);
    }

    public List<Student> searchStudents(String query) {
        return studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrRollNumberContainingIgnoreCase(
                query, query, query);
    }

    public List<Student> getStudentsByClass(String classId) {
        return studentRepository.findByStudentClass_Id(classId);
    }

    public ClassEntity getStudentClass(String username) {
        Student student = getStudentByUsername(username);
        if (student.getStudentClass() == null) {
            throw new ResourceNotFoundException("No class assigned to student with username: " + username);
        }
        return student.getStudentClass();
    }

    public Set<Event> getAllEvents() {
        return new HashSet<Event>(eventRepository.findAll());
    }

    public Set<Event> getRegisteredEvents(String username) {
        Student student = getStudentByUsername(username);
        return student.getRegisteredEvents();
    }

    @Transactional
    public Student registerForEvent(String username, String eventId) {
        Student student = getStudentByUsername(username);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        student.getRegisteredEvents().add(event);
        return studentRepository.save(student);
    }

    public Student unregisterFromEvent(String username, String eventId) {
        Student student = getStudentByUsername(username);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        student.getRegisteredEvents().remove(event);
        return studentRepository.save(student);
    }

    public List<Map<String, Object>> getAttendanceSummary(String username) {
        Student student = getStudentByUsername(username);
        // Additional logic for attendance summary would go here
        // This is just a placeholder as the specific requirements weren't shown
        return List.of();
    }

}