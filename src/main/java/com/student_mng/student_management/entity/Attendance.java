package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.BatchType;
import com.student_mng.student_management.enums.LectureSlotNumber;
import jakarta.persistence.*;

import java.time.DayOfWeek;
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

    // Store schedule info directly instead of referencing LectureSlot entity
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek weekDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureSlotNumber lectureSlotNumber;

    @Enumerated(EnumType.STRING)
    private BatchType batch; // B1, B2, NULL (for THEORY)

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean present;

    @Column(nullable = false)
    private int slotNumber = 1;

    public Attendance () {}

    public Attendance(Student student, TeacherAssignment teacherAssignment,
                      DayOfWeek weekDay, LectureSlotNumber lectureSlotNumber, BatchType batch, LocalDate date, boolean present) {
        this.student = student;
        this.teacherAssignment = teacherAssignment;
        this.weekDay = weekDay;
        this.lectureSlotNumber = lectureSlotNumber;
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

    public DayOfWeek getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(DayOfWeek weekDay) {
        this.weekDay = weekDay;
    }

    public LectureSlotNumber getLectureSlotNumber() {
        return lectureSlotNumber;
    }

    public void setLectureSlotNumber(LectureSlotNumber lectureSlotNumber) {
        this.lectureSlotNumber = lectureSlotNumber;
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

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }
}
