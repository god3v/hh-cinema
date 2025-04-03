package com.cinema.adapter.in.web.dto.mapper;

import com.cinema.adapter.in.web.dto.request.ReservationRequest;
import com.cinema.adapter.in.web.dto.response.ReservationResponse;
import com.cinema.application.dto.CreateReservationCommand;
import com.cinema.application.dto.CreateReservationResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationMapper {
    public static CreateReservationCommand toCommand(ReservationRequest request) {
        return new CreateReservationCommand(
                request.userId(),
                request.scheduleId(),
                request.seatIds()
        );
    }

    public static ReservationResponse toResponse(CreateReservationResult result) {
        return new ReservationResponse(
                result.reservationId(),
                result.movieId(),
                result.title(),
                result.screenId(),
                result.screenName(),
                result.scheduleId(),
                result.startedAt(),
                result.endedAt(),
                result.seatId(),
                result.row(),
                result.col()
        );
    }
}
