package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.TeacherAssignment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, String> {
    
}
