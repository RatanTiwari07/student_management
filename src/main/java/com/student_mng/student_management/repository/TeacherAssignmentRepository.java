package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.ClassEntity;
import com.student_mng.student_management.entity.TeacherAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, String> {

    List<TeacherAssignment> findByAssignedClassOrderByLectureSlot(ClassEntity studentClass);
}
