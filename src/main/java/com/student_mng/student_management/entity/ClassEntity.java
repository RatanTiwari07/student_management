package com.student_mng.student_management.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className; // Example: "3rd Year - Section A"

    @OneToMany(mappedBy = "studentClass", cascade = CascadeType.ALL)
    private List<Student> students;
}

