package com.cinema.application.validator;

import com.cinema.application.port.out.ScreenSeatPort;
import com.cinema.application.port.out.TicketReservationPort;
import com.cinema.domain.exception.CoreException;
import com.cinema.domain.exception.ErrorType;
import com.cinema.domain.model.ScreenSeat;
import com.cinema.domain.model.TicketReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final int MAX_RESERVE_COUNT = 5;

    private final ScreenSeatPort seatPort;
    private final TicketReservationPort reservationPort;

    public void validate(TicketReservation reservation) {
        validate(reservation, getNotReservedSeats(reservation), getReservedSeatIds(reservation));
    }

    void validate(TicketReservation reservation,
                  List<ScreenSeat> seats, List<Long> reservedSeatIds) {
        // 좌석이 예약되어 있지 않은지 검증
        if (reservation.seats().size() != seats.size()) {
            throw new CoreException(ErrorType.INVALID_SEAT_SELECTION, "이용 가능한 좌석:" + Arrays.toString(seats.toArray()));
        }

        // 이미 예약한 좌석 수 + 예약하려는 좌석 수가 최대 예약 가능 좌석 수를 초과하지 않는지 검증
        if (reservedSeatIds.size() + reservation.seats().size() > MAX_RESERVE_COUNT) {
            throw new CoreException(ErrorType.SEAT_RESERVATION_EXCEEDED, "이미 예약된 좌석 수: " + reservedSeatIds.size());
        }

        // 예약하려는 좌석이 연속적으로 붙어있는지 확인
        if (!reservation.checkSeatsContinuous(seats)) {
            throw new CoreException(ErrorType.SEATS_NOT_CONTINUOUS, "예약하려는 좌석: " + Arrays.toString(seats.toArray()));
        }
    }

    private List<ScreenSeat> getNotReservedSeats(TicketReservation reservation) {
        return seatPort.findSeatsNotReservedByScheduleIdAndSeatIds(reservation.schedule().id(), reservation.seats().stream().map(ScreenSeat::id).toList());
    }

    private List<Long> getReservedSeatIds(TicketReservation reservation) {
        return reservationPort.findIdByScheduleIdAndUserId(reservation.schedule().id(), reservation.userId());
    }
}
