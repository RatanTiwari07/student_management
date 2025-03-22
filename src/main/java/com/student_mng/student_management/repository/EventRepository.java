package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, String> {
    
}
