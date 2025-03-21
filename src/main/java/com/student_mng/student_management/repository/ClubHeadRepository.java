package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.ClassEntity;
import com.student_mng.student_management.entity.ClubHead;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClubHeadRepository extends JpaRepository<ClubHead, String> {
    
}
