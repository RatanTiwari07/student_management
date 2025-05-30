package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);
    
    boolean existsByEmail(String email);
}
