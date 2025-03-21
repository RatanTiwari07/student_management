package com.student_mng.student_management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("CLUB_HEAD") // Differentiates club heads in the User table
public class ClubHead extends User {

    private String clubName;

    @OneToMany(mappedBy = "clubHead", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();  // Events organized by this ClubHead
}


