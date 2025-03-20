package com.cinema.application.mapper;

import com.cinema.application.dto.MovieScheduleQuery;
import com.cinema.application.dto.MovieScheduleQueryResult;
import com.cinema.domain.enums.MovieGenre;
import com.cinema.domain.model.MovieSchedule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MovieScheduleApplicationMapper {

    public static MovieSchedule toDomain(MovieScheduleQuery dto) {
        return MovieSchedule.builder()
                .title(dto.title())
                .genre(MovieGenre.fromDescription(dto.genre()))
                .build();
    }

    public static MovieScheduleQueryResult toDTO(MovieSchedule schedule) {
        return MovieScheduleQueryResult.builder()
                .movieId(schedule.id())
                .title(schedule.title())
                .rating(schedule.movieRating().getDescription())
                .releaseDate(schedule.releaseDate())
                .thumbnailUrl(schedule.thumbnailUrl())
                .runningTime(schedule.runningTime())
                .genre(schedule.genre().getDescription())
                .schedules(new ArrayList<>())
                .build();
    }
}
