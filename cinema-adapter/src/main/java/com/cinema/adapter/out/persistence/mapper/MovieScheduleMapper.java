package com.cinema.adapter.out.persistence.mapper;

import com.cinema.adapter.out.persistence.projection.MovieScheduleProjection;
import com.cinema.domain.model.MovieSchedule;

public class MovieScheduleMapper {

    public static MovieSchedule toDomain(MovieScheduleProjection projection) {
        return new MovieSchedule(
                projection.getId(),
                projection.getTitle(),
                projection.getMovieRating(),
                projection.getReleaseDate(),
                projection.getThumbnailUrl(),
                projection.getRunningTime(),
                projection.getGenre(),
                projection.getScreenName(),
                projection.getStartAt(),
                projection.getEndAt()
        );
    }
}
