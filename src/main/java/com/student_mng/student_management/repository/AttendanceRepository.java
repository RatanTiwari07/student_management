package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.Attendance;
import com.student_mng.student_management.entity.Student;
import com.student_mng.student_management.entity.TeacherAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findByStudentOrderByDateDesc(Student student);
    
    List<Attendance> findByStudentAndDateBetweenOrderByDateDesc(
            Student student, LocalDate startDate, LocalDate endDate);
    
    List<Attendance> findByStudentAndTeacherAssignment_Subject_IdOrderByDateDesc(
            Student student, String subjectId);
    
    List<Attendance> findByStudentAndTeacherAssignment_Subject_IdAndDateBetweenOrderByDateDesc(
            Student student, String subjectId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student " +
           "AND a.teacherAssignment.subject.id = :subjectId AND a.present = true")
    Long countPresentAttendances(@Param("student") Student student, @Param("subjectId") String subjectId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student " +
           "AND a.teacherAssignment.subject.id = :subjectId")
    Long countTotalAttendances(@Param("student") Student student, @Param("subjectId") String subjectId);
    
    @Query("SELECT DISTINCT a.student FROM Attendance a " +
           "WHERE a.teacherAssignment.subject.id = :subjectId " +
           "GROUP BY a.student " +
           "HAVING (SUM(CASE WHEN a.present = true THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) < :threshold")
    List<Student> findStudentsBelowAttendanceThreshold(
            @Param("subjectId") String subjectId, @Param("threshold") Double threshold);

    boolean existsByTeacherAssignmentAndDate(TeacherAssignment assignment, LocalDate date);
    
    List<Attendance> findByTeacherAssignmentAndDateBetweenOrderByDateDesc(
            TeacherAssignment assignment, LocalDate startDate, LocalDate endDate);
    
    List<Attendance> findByTeacherAssignmentOrderByDateDesc(TeacherAssignment assignment);
}