package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.Student;
import com.student_mng.student_management.enums.BatchType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findByUsername(String username);
    
    List<Student> findByStudentClass_Id(String classId);
    
    List<Student> findByStudentClass_IdAndBatch(String classId, BatchType batch);
    
    List<Student> findByStudentClassIsNull();
    
    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrRollNumberContainingIgnoreCase(
            String firstName, String lastName, String rollNumber);
}