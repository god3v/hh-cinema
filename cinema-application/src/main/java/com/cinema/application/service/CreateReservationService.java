package com.cinema.application.service;

import com.cinema.application.dto.CreateReservationCommand;
import com.cinema.application.dto.CreateReservationResult;
import com.cinema.application.dto.ReservedEvent;
import com.cinema.application.mapper.ReservationMapper;
import com.cinema.application.port.in.CreateReservationUseCase;
import com.cinema.application.port.out.DistributedLock;
import com.cinema.application.port.out.TicketReservationPort;
import com.cinema.application.validator.ReservationValidator;
import com.cinema.domain.model.TicketReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateReservationService implements CreateReservationUseCase {

    private final ReservationValidator reservationValidator;
    private final TicketReservationPort reservationPort;
    private final DistributedLock lock;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<CreateReservationResult> createReservation(CreateReservationCommand command) {
        TicketReservation reservation = ReservationMapper.toDomain(command);

        String lockKey = String.valueOf(command.scheduleId());
        return lock.executeWithLock(lockKey, 3, 3, () -> {
            reservationValidator.validate(reservation);

            List<Long> reservedIds = reservationPort.saveReservations(reservation);

            List<CreateReservationResult> reservations = reservationPort.findReservations(reservedIds);

            eventPublisher.publishEvent(new ReservedEvent(reservations));

            return reservations;
        });
    }
}
