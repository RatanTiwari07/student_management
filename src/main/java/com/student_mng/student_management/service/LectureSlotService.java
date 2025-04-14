package com.student_mng.student_management.service;

import com.student_mng.student_management.entity.LectureSlot;
import com.student_mng.student_management.enums.LectureSlotNumber;
import com.student_mng.student_management.repository.LectureSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;

@Service
@Transactional(readOnly = true)
public class LectureSlotService {
    
    private final LectureSlotRepository lectureSlotRepository;

    public LectureSlotService(LectureSlotRepository lectureSlotRepository) {
        this.lectureSlotRepository = lectureSlotRepository;
    }

//    public List<LectureSlot> getAvailableSlots(DayOfWeek weekDay) {
//        return lectureSlotRepository.findByWeekDayOrderBySlotNumber(weekDay);
//    }
//
//    public Optional<LectureSlot> findSlot(DayOfWeek weekDay, LectureSlotNumber slotNumber) {
//        return lectureSlotRepository.findByWeekDayAndSlotNumber(weekDay, slotNumber);
//    }

    @Transactional
    public LectureSlot createSlot(DayOfWeek weekDay, LectureSlotNumber slotNumber) {
        LectureSlot slot = new LectureSlot(weekDay, slotNumber);
        return lectureSlotRepository.save(slot);
    }
}