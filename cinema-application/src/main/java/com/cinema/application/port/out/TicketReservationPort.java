package com.cinema.application.port.out;

import com.cinema.application.dto.CreateReservationResult;
import com.cinema.domain.model.TicketReservation;

import java.util.List;

public interface TicketReservationPort {

    List<Long> findIdByScheduleIdAndUserId(Long scheduleId, Long userId);

    List<Long> saveReservations(TicketReservation reservation);

    List<CreateReservationResult> findReservations(List<Long> reservedIds);
}
