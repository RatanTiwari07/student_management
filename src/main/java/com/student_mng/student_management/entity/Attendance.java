package com.student_mng.student_management.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "lecture_slot_id")
    private LectureSlot lectureSlot;

    private boolean isPresent;

    public Attendance () {}

    public Attendance(LocalDate date, Student student, Teacher teacher, Subject subject,
                      LectureSlot lectureSlot, boolean isPresent) {
        this.date = date;
        this.student = student;
        this.teacher = teacher;
        this.subject = subject;
        this.lectureSlot = lectureSlot;
        this.isPresent = isPresent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LectureSlot getLectureSlot() {
        return lectureSlot;
    }

    public void setLectureSlot(LectureSlot lectureSlot) {
        this.lectureSlot = lectureSlot;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}

