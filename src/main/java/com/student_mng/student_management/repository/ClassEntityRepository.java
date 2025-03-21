package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClassEntityRepository extends JpaRepository<ClassEntity, String> {
    
}
