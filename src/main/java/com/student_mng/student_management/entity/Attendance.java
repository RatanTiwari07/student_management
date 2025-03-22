package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.BatchType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_assignment_id", nullable = false)
    private TeacherAssignment teacherAssignment;

    @ManyToOne
    @JoinColumn(name = "lecture_slot_id", nullable = false)
    private LectureSlot lectureSlot;

    @Enumerated(EnumType.STRING)
    private BatchType batch; // B1, B2, NULL (for THEORY)

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean present;

    public Attendance () {}

    public Attendance(Student student, TeacherAssignment teacherAssignment,
                      LectureSlot lectureSlot, BatchType batch, LocalDate date, boolean present) {
        this.student = student;
        this.teacherAssignment = teacherAssignment;
        this.lectureSlot = lectureSlot;
        this.batch = batch;
        this.date = date;
        this.present = present;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public TeacherAssignment getTeacherAssignment() {
        return teacherAssignment;
    }

    public void setTeacherAssignment(TeacherAssignment teacherAssignment) {
        this.teacherAssignment = teacherAssignment;
    }

    public LectureSlot getLectureSlot() {
        return lectureSlot;
    }

    public void setLectureSlot(LectureSlot lectureSlot) {
        this.lectureSlot = lectureSlot;
    }

    public BatchType getBatch() {
        return batch;
    }

    public void setBatch(BatchType batch) {
        this.batch = batch;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}

