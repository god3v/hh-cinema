package com.cinema.adapter.out.persistence.projection;

import com.cinema.domain.enums.MovieGenre;
import com.cinema.domain.enums.MovieRating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieScheduleProjection {
    private Long id;
    private String title;
    private MovieRating movieRating;
    private LocalDate releaseDate;
    private String thumbnailUrl;
    private int runningTime;
    private MovieGenre genre;
    private String screenName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
