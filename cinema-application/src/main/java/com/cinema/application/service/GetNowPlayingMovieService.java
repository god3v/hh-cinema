package com.cinema.application.service;

import com.cinema.application.dto.MovieSearchQuery;
import com.cinema.application.dto.MovieSearchResult;
import com.cinema.application.port.in.GetNowPlayingMovieUseCase;
import com.cinema.application.port.out.MovieSchedulePort;
import com.cinema.domain.model.MovieSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetNowPlayingMovieService implements GetNowPlayingMovieUseCase {

    private final MovieSchedulePort movieSchedulePort;

    @Override
    public List<MovieSearchResult> getNowPlayingMovies(MovieSearchQuery query) {
        List<MovieSchedule> schedules = movieSchedulePort.findNowPlayingMovies(query.genre(), query.title());

        Map<Long, MovieSearchResult> movieMap = new LinkedHashMap<>();

        for (MovieSchedule schedule : schedules) {
            MovieSearchResult movieSchedule = movieMap.computeIfAbsent(schedule.id(), id ->
                    MovieSearchResult.of(schedule));

            movieSchedule.schedules()
                    .add(MovieSearchResult.Schedule.builder()
                            .screenName(schedule.screenName())
                            .startedAt(schedule.startedAt())
                            .endedAt(schedule.endedAt())
                            .build());
        }

        return new ArrayList<>(movieMap.values());
    }
}
