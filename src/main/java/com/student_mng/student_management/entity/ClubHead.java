package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLUB_HEAD") // Differentiates club heads in the User table
public class ClubHead extends User {

    private String clubName;

    @OneToMany(mappedBy = "clubHead", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();  // Events organized by this ClubHead

    public ClubHead () {}

    public ClubHead(String username, String password, String email, Role role, String clubName, List<Event> events) {
        super(username, password, role, email);
        this.clubName = clubName;
        this.events = events;
    }

    public ClubHead(String clubName, List<Event> events) {
        this.clubName = clubName;
        this.events = events;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}


