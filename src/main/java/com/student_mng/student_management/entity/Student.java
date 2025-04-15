package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.BatchType;
import com.student_mng.student_management.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Student extends User {

    @Column(unique = true)
    private String rollNumber;

    private String firstName;
    private String lastName;
    private String contactNumber;
    private String parentContactNumber;
    private String parentEmail;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity studentClass;

    @Enumerated(EnumType.STRING)
    private BatchType batch;

    @ManyToMany
    @JoinTable(
            name = "student_events",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> registeredEvents;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getParentContactNumber() {
        return parentContactNumber;
    }

    public void setParentContactNumber(String parentContactNumber) {
        this.parentContactNumber = parentContactNumber;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }
}
