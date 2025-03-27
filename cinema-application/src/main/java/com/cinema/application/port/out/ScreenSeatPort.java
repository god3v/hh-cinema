package com.cinema.application.port.out;

import com.cinema.domain.model.ScreenSeat;

import java.util.List;

public interface ScreenSeatPort {
    List<ScreenSeat> findSeatsNotReservedByScheduleIdAndSeatIds(Long scheduleId, List<Long> seatIds);
}
