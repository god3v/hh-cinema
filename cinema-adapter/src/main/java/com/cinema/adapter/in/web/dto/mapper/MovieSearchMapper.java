package com.cinema.adapter.in.web.dto.mapper;

import com.cinema.adapter.in.web.dto.request.NowPlayingMovieRequest;
import com.cinema.adapter.in.web.dto.response.NowPlayingMovieResponse;
import com.cinema.application.dto.MovieSearchQuery;
import com.cinema.application.dto.MovieSearchResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MovieSearchMapper {
    public static MovieSearchQuery toQuery(NowPlayingMovieRequest request) {
        return MovieSearchQuery.builder()
                .title(request.title())
                .genre(request.genre())
                .build();
    }

    public static NowPlayingMovieResponse toResponse(MovieSearchResult result) {
        return NowPlayingMovieResponse.builder()
                .movieId(result.movieId())
                .title(result.title())
                .rating(result.rating())
                .releaseDate(result.releaseDate())
                .thumbnailUrl(result.thumbnailUrl())
                .runningTime(result.runningTime())
                .genre(result.genre())
                .schedules(result.schedules().stream()
                        .map(s -> new NowPlayingMovieResponse.Schedule(s.screenName(), s.startedAt(), s.endedAt()))
                        .collect(Collectors.toList()))
                .build();
    }

    public static List<NowPlayingMovieResponse> toResponseList(List<MovieSearchResult> results) {
        return results.stream()
                .map(MovieSearchMapper::toResponse)
                .collect(Collectors.toList());
    }
}
