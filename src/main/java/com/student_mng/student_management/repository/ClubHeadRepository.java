package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.ClubHead;
import com.student_mng.student_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubHeadRepository extends JpaRepository<ClubHead, String> {
    Optional<ClubHead> findByStudent(Student student);
    boolean existsByStudent(Student student);
    Optional<ClubHead> findByStudentId(String studentId);
}
