package com.student_mng.student_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LectureSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String weekDayName; // Monday, Tuesday, etc.

    private String startTime; // Example: "10:30 AM"

    private String endTime; // Example: "11:40 AM"

    public LectureSlot () {}

    public LectureSlot(String endTime, String startTime, String weekDayName) {
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getWeekDayName() {
        return weekDayName;
    }

    public void setWeekDayName(String weekDayName) {
        this.weekDayName = weekDayName;
    }
}

