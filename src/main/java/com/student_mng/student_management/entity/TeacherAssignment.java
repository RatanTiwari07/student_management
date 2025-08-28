package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.BatchType;
import com.student_mng.student_management.enums.LectureType;
import com.student_mng.student_management.enums.LectureSlotNumber;
import jakarta.persistence.*;

import java.time.DayOfWeek;

@Entity
public class TeacherAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity assignedClass;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureType lectureType; // THEORY or LAB

    @Enumerated(EnumType.STRING)
    private BatchType batch; // B1, B2 for LAB, NULL for THEORY

    // Replace LectureSlot entity with simple enum fields
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek weekDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureSlotNumber slotNumber;

    public TeacherAssignment () {}

    public TeacherAssignment(Teacher teacher, ClassEntity assignedClass, Subject subject,
                             LectureType lectureType, BatchType batch, DayOfWeek weekDay, LectureSlotNumber slotNumber) {
        this.teacher = teacher;
        this.assignedClass = assignedClass;
        this.subject = subject;
        this.lectureType = lectureType;
        this.batch = batch;
        this.weekDay = weekDay;
        this.slotNumber = slotNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public ClassEntity getAssignedClass() {
        return assignedClass;
    }

    public void setAssignedClass(ClassEntity assignedClass) {
        this.assignedClass = assignedClass;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LectureType getLectureType() {
        return lectureType;
    }

    public void setLectureType(LectureType lectureType) {
        this.lectureType = lectureType;
    }

    public BatchType getBatch() {
        return batch;
    }

    public void setBatch(BatchType batch) {
        this.batch = batch;
    }

    public DayOfWeek getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(DayOfWeek weekDay) {
        this.weekDay = weekDay;
    }

    public LectureSlotNumber getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(LectureSlotNumber slotNumber) {
        this.slotNumber = slotNumber;
    }

    // Convenience methods for getting time information
    public java.time.LocalTime getStartTime() {
        return slotNumber.getStartTime();
    }

    public java.time.LocalTime getEndTime() {
        return slotNumber.getEndTime();
    }
}
