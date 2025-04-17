package com.student_mng.student_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AttendanceReport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private double attendancePercentage;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDate generatedDate;

    public AttendanceReport(Student student, double percentage, LocalDate startDate, LocalDate endDate) {
        this.student = student;
        this.attendancePercentage = percentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedDate = LocalDate.now();
    }
}