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
@DiscriminatorValue("STUDENT") // Differentiates student in the User table
public class Student extends User {

    private String rollNumber;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity studentClass;
}
