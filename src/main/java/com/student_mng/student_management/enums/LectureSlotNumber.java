package com.student_mng.student_management.enums;

import java.time.LocalTime;

public enum LectureSlotNumber {
    SLOT_1(LocalTime.of(8, 20), LocalTime.of(9, 30)),
    SLOT_2(LocalTime.of(9, 30), LocalTime.of(10, 30)),
    SLOT_3(LocalTime.of(10, 30), LocalTime.of(11, 30)),
    SLOT_4(LocalTime.of(11, 30), LocalTime.of(12, 30)),
    SLOT_5(LocalTime.of(12, 30), LocalTime.of(13, 30)),
    SLOT_6(LocalTime.of(13, 30), LocalTime.of(14, 30)),
    SLOT_7(LocalTime.of(14, 30), LocalTime.of(15, 30)),
    SLOT_8(LocalTime.of(15, 30), LocalTime.of(16, 30)),
    SLOT_9(LocalTime.of(16, 30), LocalTime.of(17, 0));

    private final LocalTime startTime;
    private final LocalTime endTime;

    LectureSlotNumber(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}