package com.cinema.adapter.out.persistence;

import com.cinema.adapter.out.persistence.entity.*;
import com.cinema.adapter.out.persistence.repository.TicketReservationJpaRepository;
import com.cinema.application.dto.CreateReservationResult;
import com.cinema.application.port.out.TicketReservationPort;
import com.cinema.domain.model.TicketReservation;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TicketReservationAdapter implements TicketReservationPort {

    private final TicketReservationJpaRepository repository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findIdByScheduleIdAndUserId(Long scheduleId, Long userId) {
        QTicketReservationEntity reservation = QTicketReservationEntity.ticketReservationEntity;
        return queryFactory
                .select(reservation.id)
                .from(reservation)
                .where(reservation.screeningSchedule.id.eq(scheduleId)
                        .and(reservation.user.id.eq(userId)))
                .fetch();
    }

    @Override
    public List<Long> saveReservations(TicketReservation reservation) {
        List<TicketReservationEntity> entities = reservation.seats().stream()
                .map(seat -> TicketReservationEntity.create(
                        new ScreeningScheduleEntity(reservation.schedule().id()),
                        new ScreenSeatEntity(seat.id()),
                        new UserEntity(reservation.userId())
                ))
                .toList();
        return repository.saveAll(entities).stream()
                .map(TicketReservationEntity::getId)
                .toList();
    }

    @Override
    public List<CreateReservationResult> findReservations(List<Long> reservedIds) {
        QTicketReservationEntity reservation = QTicketReservationEntity.ticketReservationEntity;
        QScreeningScheduleEntity schedule = QScreeningScheduleEntity.screeningScheduleEntity;
        QScreenSeatEntity seat = QScreenSeatEntity.screenSeatEntity;
        QScreenEntity screen = QScreenEntity.screenEntity;
        QMovieEntity movie = QMovieEntity.movieEntity;

        return queryFactory
                .select(Projections.constructor(
                        CreateReservationResult.class,
                        reservation.id,
                        movie.id,
                        movie.title,
                        screen.id,
                        screen.screenName,
                        schedule.id,
                        schedule.startedAt,
                        schedule.endedAt,
                        seat.id,
                        seat.row,
                        seat.col
                ))
                .from(reservation)
                .leftJoin(schedule).on(schedule.id.eq(reservation.screeningSchedule.id))
                .leftJoin(screen).on(screen.id.eq(schedule.screen.id))
                .leftJoin(seat).on(seat.id.eq(reservation.screenSeat.id))
                .leftJoin(movie).on(movie.id.eq(schedule.movie.id))
                .where(reservation.id.in(reservedIds))
                .fetch();
    }
}
