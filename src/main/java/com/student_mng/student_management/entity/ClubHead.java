package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.Role;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLUB_HEAD") // Differentiates club heads in the User table
public class ClubHead extends User {

    private String clubName;

    // Reference to the student who is the club head
    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @OneToMany(mappedBy = "clubHead", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();  // Events organized by this ClubHead

    public ClubHead () {}

    public ClubHead(String username, String password, String email, Role role, String clubName, Student student, List<Event> events) {
        super(username, password, role, email);
        this.clubName = clubName;
        this.student = student;
        this.events = events;
    }

    public ClubHead(String clubName, Student student, List<Event> events) {
        this.clubName = clubName;
        this.student = student;
        this.events = events;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
