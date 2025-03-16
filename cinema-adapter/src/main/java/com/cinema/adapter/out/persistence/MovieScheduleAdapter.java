package com.cinema.adapter.out.persistence;

import com.cinema.adapter.out.persistence.entity.QMovieEntity;
import com.cinema.adapter.out.persistence.entity.QScreenEntity;
import com.cinema.adapter.out.persistence.entity.QScreeningScheduleEntity;
import com.cinema.adapter.out.persistence.mapper.MovieScheduleMapper;
import com.cinema.adapter.out.persistence.projection.MovieScheduleProjection;
import com.cinema.application.port.out.MovieSchedulePort;
import com.cinema.domain.model.MovieSchedule;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MovieScheduleAdapter implements MovieSchedulePort {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MovieSchedule> findNowPlayingMovies() {
        QMovieEntity movie = QMovieEntity.movieEntity;
        QScreeningScheduleEntity schedule = QScreeningScheduleEntity.screeningScheduleEntity;
        QScreenEntity screen = QScreenEntity.screenEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(schedule.startAt.after(LocalDateTime.now())) // 상영 시작 시간이 현재 시간보다 이후
                .and(movie.releaseDate.before(LocalDate.now().plusDays(1))); // 개봉일이 오늘을 포함한 날짜보다 이전

        List<MovieScheduleProjection> fetch = queryFactory
                .select(Projections.bean(
                        MovieScheduleProjection.class,
                        movie.id,
                        movie.title,
                        movie.movieRating,
                        movie.releaseDate,
                        movie.thumbnailUrl,
                        movie.runningTime,
                        movie.genre,
                        screen.screenName,
                        schedule.startAt,
                        schedule.endAt
                ))
                .from(schedule)
                .innerJoin(movie).on(movie.id.eq(schedule.movie.id))
                .innerJoin(screen).on(screen.id.eq(schedule.screen.id))
                .where(builder)
                .orderBy(movie.releaseDate.desc(), schedule.startAt.asc())
                .fetch();

        return fetch.stream()
                .map(MovieScheduleMapper::toDomain)
                .collect(Collectors.toList());
    }
}
