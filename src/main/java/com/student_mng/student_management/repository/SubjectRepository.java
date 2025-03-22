package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, String> {
    
}
