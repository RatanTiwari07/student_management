package com.student_mng.student_management.repository;

import com.student_mng.student_management.entity.AttendanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceReportRepository extends JpaRepository<AttendanceReport, String> {
}