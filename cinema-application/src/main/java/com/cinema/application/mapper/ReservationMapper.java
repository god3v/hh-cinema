package com.cinema.application.mapper;

import com.cinema.application.dto.CreateReservationCommand;
import com.cinema.domain.model.ScreenSeat;
import com.cinema.domain.model.ScreeningSchedule;
import com.cinema.domain.model.TicketReservation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationMapper {

    public static TicketReservation toDomain(CreateReservationCommand command) {
        return TicketReservation.builder()
                .schedule(ScreeningSchedule.builder().id(command.scheduleId()).build())
                .seats(command.seatIds().stream()
                        .map(id -> ScreenSeat.builder().id(id).build())
                        .toList())
                .userId(command.userId())
                .build();
    }
}
