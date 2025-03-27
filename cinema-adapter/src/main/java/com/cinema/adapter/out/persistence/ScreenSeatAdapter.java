package com.cinema.adapter.out.persistence;

import com.cinema.adapter.out.persistence.entity.QScreenSeatEntity;
import com.cinema.adapter.out.persistence.entity.QTicketReservationEntity;
import com.cinema.application.port.out.ScreenSeatPort;
import com.cinema.domain.model.ScreenSeat;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScreenSeatAdapter implements ScreenSeatPort {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ScreenSeat> findSeatsNotReservedByScheduleIdAndSeatIds(Long scheduleId, List<Long> seatIds) {
        QScreenSeatEntity seat = QScreenSeatEntity.screenSeatEntity;
        QTicketReservationEntity reservation = QTicketReservationEntity.ticketReservationEntity;

        return queryFactory
                .select(Projections.constructor(
                        ScreenSeat.class,
                        seat.id,
                        seat.screen.id,
                        seat.row,
                        seat.col))
                .from(seat)
                .leftJoin(reservation)
                .on(reservation.screenSeat.eq(seat)
                        .and(reservation.screeningSchedule.id.eq(scheduleId)))
                .where(seat.id.in(seatIds)
                        .and(reservation.id.isNull()))
                .fetch();
    }
}
