package com.cinema.application.service;

import com.cinema.application.dto.CreateReservationCommand;
import com.cinema.application.dto.CreateReservationResult;
import com.cinema.application.event.MessageService;
import com.cinema.application.mapper.ReservationMapper;
import com.cinema.application.port.in.CreateReservationUseCase;
import com.cinema.application.port.out.TicketReservationPort;
import com.cinema.common.aop.DistributedLock;
import com.cinema.common.aop.DistributedLockUtil;
import com.cinema.domain.model.TicketReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateReservationService implements CreateReservationUseCase {

    private final ReservationValidator reservationValidator;
    private final TicketReservationPort reservationPort;
    private final DistributedLockUtil lockUtil;
    private final MessageService messageService;

    @Override
    @Transactional
//    @DistributedLock(key = "#command.scheduleId")
    public List<CreateReservationResult> createReservation(CreateReservationCommand command) {
        TicketReservation reservation = ReservationMapper.toDomain(command);

        String lockKey = "lock:" + command.scheduleId();
        return lockUtil.executeWithLock(lockKey, 5, 3, () -> {
            reservationValidator.validate(reservation);

            List<Long> reservedIds = reservationPort.saveReservations(reservation);

            List<CreateReservationResult> reservations = reservationPort.findReservations(reservedIds);
            messageService.send(reservations);
            return reservations;
        });
    }
}
