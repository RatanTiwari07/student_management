package com.student_mng.student_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
public class LectureSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    private DayOfWeek weekDayName; // Monday, Tuesday, etc.

    private LocalTime startTime; // Example: "10:30 AM"

    private LocalTime endTime; // Example: "11:40 AM"

    public LectureSlot () {}

    public LectureSlot(LocalTime endTime, LocalTime startTime, DayOfWeek weekDayName) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.weekDayName = weekDayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public DayOfWeek getWeekDayName() {
        return weekDayName;
    }

    public void setWeekDayName(DayOfWeek weekDayName) {
        this.weekDayName = weekDayName;
    }
}

