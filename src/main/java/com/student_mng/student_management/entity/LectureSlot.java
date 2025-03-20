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
public class LectureSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String weekDayName; // Monday, Tuesday, etc.

    private String startTime; // Example: "10:30 AM"

    private String endTime; // Example: "11:40 AM"
}

