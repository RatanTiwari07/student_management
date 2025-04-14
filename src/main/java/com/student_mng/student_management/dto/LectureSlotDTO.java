package com.student_mng.student_management.dto;

import com.student_mng.student_management.enums.LectureSlotNumber;
import java.time.DayOfWeek;

public class LectureSlotDTO {
    private String id;
    private DayOfWeek weekDay;
    private LectureSlotNumber slotNumber;

    // Default constructor
    public LectureSlotDTO() {}

    // Constructor with fields
    public LectureSlotDTO(String id, DayOfWeek weekDay, LectureSlotNumber slotNumber) {
        this.id = id;
        this.weekDay = weekDay;
        this.slotNumber = slotNumber;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DayOfWeek getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(DayOfWeek weekDay) {
        this.weekDay = weekDay;
    }

    public LectureSlotNumber getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(LectureSlotNumber slotNumber) {
        this.slotNumber = slotNumber;
    }
}