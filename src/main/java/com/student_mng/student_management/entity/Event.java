package com.student_mng.student_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
