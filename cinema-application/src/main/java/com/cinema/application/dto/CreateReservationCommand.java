package com.cinema.application.dto;

import java.util.List;

public record CreateReservationCommand(
        Long userId,
        Long scheduleId,
        List<Long> seatIds
) {
}
