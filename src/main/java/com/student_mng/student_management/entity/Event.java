package com.student_mng.student_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Event {

    @Id
    private String id; // Unique event ID (String-based as per previous decisions)

    private String name;
    private LocalDateTime eventDateTime;  // Stores both date & time of the event

    @ManyToOne
    @JoinColumn(name = "club_head_id")
    private ClubHead clubHead;  // Event is organized by a ClubHead

    @ManyToMany
    @JoinTable(
            name = "student_event_registration",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> registeredStudents = new HashSet<>();

    public Event () {}

    public Event(String name, LocalDateTime eventDateTime, ClubHead clubHead,
                 Set<Student> registeredStudents) {
        this.name = name;
        this.eventDateTime = eventDateTime;
        this.clubHead = clubHead;
        this.registeredStudents = registeredStudents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public ClubHead getClubHead() {
        return clubHead;
    }

    public void setClubHead(ClubHead clubHead) {
        this.clubHead = clubHead;
    }

    public Set<Student> getRegisteredStudents() {
        return registeredStudents;
    }

    public void setRegisteredStudents(Set<Student> registeredStudents) {
        this.registeredStudents = registeredStudents;
    }
}
