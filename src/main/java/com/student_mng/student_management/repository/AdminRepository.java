package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {

}
