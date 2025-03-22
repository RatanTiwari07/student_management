package com.student_mng.student_management.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TeacherAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity assignedClass;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "lecture_slot_id")
    private LectureSlot lectureSlot;

    public TeacherAssignment () {}

    public TeacherAssignment(Teacher teacher, ClassEntity assignedClass, Subject subject,
                             LectureSlot lectureSlot) {
        this.teacher = teacher;
        this.assignedClass = assignedClass;
        this.subject = subject;
        this.lectureSlot = lectureSlot;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LectureSlot getLectureSlot() {
        return lectureSlot;
    }

    public void setLectureSlot(LectureSlot lectureSlot) {
        this.lectureSlot = lectureSlot;
    }
}

