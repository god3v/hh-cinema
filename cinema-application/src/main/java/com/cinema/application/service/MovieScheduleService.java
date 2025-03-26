package com.cinema.application.service;

import com.cinema.application.dto.MovieScheduleQuery;
import com.cinema.application.dto.MovieScheduleQueryResult;
import com.cinema.application.port.in.MovieScheduleUseCase;
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
public class MovieScheduleService implements MovieScheduleUseCase {

    private final MovieSchedulePort movieSchedulePort;

    @Override
    public List<MovieScheduleQueryResult> getNowPlayingMovies(MovieScheduleQuery query) {
        List<MovieSchedule> schedules = movieSchedulePort.findNowPlayingMovies(query.genre(), query.title());

        Map<Long, MovieScheduleQueryResult> movieMap = new LinkedHashMap<>();

        for (MovieSchedule schedule : schedules) {
            MovieScheduleQueryResult movieSchedule = movieMap.computeIfAbsent(schedule.id(), id ->
                    MovieScheduleQueryResult.of(schedule));

            movieSchedule.schedules()
                    .add(MovieScheduleQueryResult.Schedule.builder()
                            .screenName(schedule.screenName())
                            .startedAt(schedule.startedAt())
                            .endedAt(schedule.endedAt())
                            .build());
        }

        return new ArrayList<>(movieMap.values());
    }
}
