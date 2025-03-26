package com.cinema.application.dto;

import com.cinema.domain.model.MovieSchedule;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
public record MovieScheduleQueryResult(
        Long movieId,
        String title,
        String rating,
        LocalDate releaseDate,
        String thumbnailUrl,
        int runningTime,
        String genre,
        List<Schedule> schedules
) {

    public static MovieScheduleQueryResult of(MovieSchedule schedule) {
        return new MovieScheduleQueryResult(
                schedule.id(),
                schedule.title(),
                schedule.movieRating().name(),
                schedule.releaseDate(),
                schedule.thumbnailUrl(),
                schedule.runningTime(),
                schedule.genre().name(),
                new ArrayList<>()
        );
    }

    @Builder
    public record Schedule (
            String screenName,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
    }
}
