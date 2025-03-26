package com.cinema.adapter.in.web.dto.mapper;

import com.cinema.adapter.in.web.dto.request.MovieScheduleRequest;
import com.cinema.adapter.in.web.dto.response.MovieScheduleResponse;
import com.cinema.application.dto.MovieScheduleQuery;
import com.cinema.application.dto.MovieScheduleQueryResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MovieScheduleMapper {
    public static MovieScheduleQuery toQuery(MovieScheduleRequest request) {
        return MovieScheduleQuery.builder()
                .title(request.title())
                .genre(request.genre())
                .build();
    }

    public static MovieScheduleResponse toResponse(MovieScheduleQueryResult result) {
        return MovieScheduleResponse.builder()
                .movieId(result.movieId())
                .title(result.title())
                .rating(result.rating())
                .releaseDate(result.releaseDate())
                .thumbnailUrl(result.thumbnailUrl())
                .runningTime(result.runningTime())
                .genre(result.genre())
                .schedules(result.schedules().stream()
                        .map(s -> new MovieScheduleResponse.Schedule(s.screenName(), s.startedAt(), s.endedAt()))
                        .collect(Collectors.toList()))
                .build();
    }

    public static List<MovieScheduleResponse> toResponseList(List<MovieScheduleQueryResult> results) {
        return results.stream()
                .map(MovieScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }
}
