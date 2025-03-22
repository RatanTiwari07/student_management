package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    private String rollNumber;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity studentClass;

    @ManyToMany(mappedBy = "registeredStudents")
    private Set<Event> registeredEvents = new HashSet<>();

    public Student () {}

    public Student(String username, String password, Role role, String rollNumber,
                   ClassEntity studentClass, Set<Event> registeredEvents) {
        super(username, password, role);
        this.rollNumber = rollNumber;
        this.studentClass = studentClass;
        this.registeredEvents = registeredEvents;
    }

    public Student(String rollNumber, ClassEntity studentClass, Set<Event> registeredEvents) {
        this.rollNumber = rollNumber;
        this.studentClass = studentClass;
        this.registeredEvents = registeredEvents;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public Set<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    public void setRegisteredEvents(Set<Event> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }

    public ClassEntity getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(ClassEntity studentClass) {
        this.studentClass = studentClass;
    }
}
