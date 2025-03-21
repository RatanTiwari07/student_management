package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.Event;
import com.student_mng.student_management.entity.LectureSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureSlotRepository extends JpaRepository<LectureSlot, String> {
    
}
