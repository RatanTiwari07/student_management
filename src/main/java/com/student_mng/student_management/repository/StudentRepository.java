package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByStudentClassIsNull();
}
