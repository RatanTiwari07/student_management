package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.LectureSlotNumber;
import jakarta.persistence.*;
import java.time.DayOfWeek;

@Entity
public class LectureSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek weekDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureSlotNumber slotNumber;

    public LectureSlot() {}

    public LectureSlot(DayOfWeek weekDay, LectureSlotNumber slotNumber) {
        this.weekDay = weekDay;
        this.slotNumber = slotNumber;
    }

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

    // Convenience methods
    public java.time.LocalTime getStartTime() {
        return slotNumber.getStartTime();
    }

    public java.time.LocalTime getEndTime() {
        return slotNumber.getEndTime();
    }
}

