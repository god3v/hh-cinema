package com.cinema.adapter.out.persistence;

import com.cinema.adapter.out.persistence.entity.QMovieEntity;
import com.cinema.adapter.out.persistence.entity.QScreenEntity;
import com.cinema.adapter.out.persistence.entity.QScreeningScheduleEntity;
import com.cinema.adapter.out.persistence.mapper.MovieSchedulePersistenceMapper;
import com.cinema.adapter.out.persistence.projection.MovieScheduleProjection;
import com.cinema.application.port.out.MovieSchedulePort;
import com.cinema.domain.enums.MovieGenre;
import com.cinema.domain.model.MovieSchedule;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MovieScheduleAdapter implements MovieSchedulePort {

    private final JPAQueryFactory queryFactory;

    @Override
    @Cacheable(value = "playing_movies",
            key = "'movies:' + (#p0 != null ? #p0 : 'ALL') + (#p1 != null ? (':' + #p1) : '')",
            cacheManager = "redisCacheManager")
    public List<MovieSchedule> findNowPlayingMovies(String genre, String title) {
        QMovieEntity movie = QMovieEntity.movieEntity;
        QScreeningScheduleEntity schedule = QScreeningScheduleEntity.screeningScheduleEntity;
        QScreenEntity screen = QScreenEntity.screenEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(schedule.startedAt.after(LocalDateTime.now())); // 상영 시작 시간이 현재 시간보다 이후

        if (title != null) {
            builder.and(Expressions.booleanTemplate(
                    "function('match_against', {0}, {1}) > 0",
                    movie.title,
                    title
            ));
        }
        if (genre != null) {
            builder.and(movie.genre.eq(MovieGenre.valueOf(genre)));
        }

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
                        schedule.startedAt,
                        schedule.endedAt
                ))
                .from(movie)
                .innerJoin(schedule).on(schedule.movie.eq(movie))
                .innerJoin(screen).on(screen.eq(schedule.screen))
                .where(builder)
                .orderBy(movie.releaseDate.desc(), schedule.startedAt.asc())
                .fetch();

        return fetch.stream()
                .map(MovieSchedulePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
