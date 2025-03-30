package com.cinema.application.service;

import com.cinema.application.dto.CreateReservationCommand;
import com.cinema.application.dto.CreateReservationResult;
import com.cinema.application.mapper.ReservationMapper;
import com.cinema.application.port.in.CreateReservationUseCase;
import com.cinema.application.port.out.TicketReservationPort;
import com.cinema.domain.model.TicketReservation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateReservationService implements CreateReservationUseCase {

    private final ReservationValidator reservationValidator;
    private final TicketReservationPort reservationPort;

    @Override
//    @Transactional
    @DistributedLock(key = "#command.scheduleId")
    public List<CreateReservationResult> createReservation(CreateReservationCommand command) {
        TicketReservation reservation = ReservationMapper.toDomain(command);

        // 예약 유효성 검증
        reservationValidator.validate(reservation);

        List<Long> reservedIds = reservationPort.saveReservations(reservation);

        return reservationPort.findReservations(reservedIds);
    }
}
