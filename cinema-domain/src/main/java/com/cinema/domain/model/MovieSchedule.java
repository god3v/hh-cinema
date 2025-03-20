package com.cinema.domain.model;

import com.cinema.domain.enums.MovieGenre;
import com.cinema.domain.enums.MovieRating;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record MovieSchedule(
        Long id,
        String title,
        MovieRating movieRating,
        LocalDate releaseDate,
        String thumbnailUrl,
        int runningTime,
        MovieGenre genre,
        String screenName,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {
}
