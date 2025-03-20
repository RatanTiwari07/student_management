package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
