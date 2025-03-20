package com.cinema.adapter.out.persistence.mapper;

import com.cinema.adapter.out.persistence.projection.MovieScheduleProjection;
import com.cinema.domain.model.MovieSchedule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MovieSchedulePersistenceMapper {

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
                projection.getStartedAt(),
                projection.getEndedAt()
        );
    }
}
