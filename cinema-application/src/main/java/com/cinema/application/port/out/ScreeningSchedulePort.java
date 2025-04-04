package com.cinema.application.port.out;

import com.cinema.domain.model.ScreeningSchedule;

public interface ScreeningSchedulePort {

    ScreeningSchedule findById(Long scheduleId);
}
