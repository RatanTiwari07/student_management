package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeacherRepository extends JpaRepository<Teacher, String> {
    
}
