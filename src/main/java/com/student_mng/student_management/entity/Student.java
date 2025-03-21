package com.student_mng.student_management.entity;

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
}
