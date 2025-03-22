package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.BatchType;
import com.student_mng.student_management.enums.LectureType;
import jakarta.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "lecture_slot_id", nullable = false)
    private LectureSlot lectureSlot;

    public TeacherAssignment () {}

    public TeacherAssignment(Teacher teacher, ClassEntity assignedClass, Subject subject,
                             LectureType lectureType, BatchType batch, LectureSlot lectureSlot) {
        this.teacher = teacher;
        this.assignedClass = assignedClass;
        this.subject = subject;
        this.lectureType = lectureType;
        this.batch = batch;
        this.lectureSlot = lectureSlot;
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

    public LectureSlot getLectureSlot() {
        return lectureSlot;
    }

    public void setLectureSlot(LectureSlot lectureSlot) {
        this.lectureSlot = lectureSlot;
    }
}

