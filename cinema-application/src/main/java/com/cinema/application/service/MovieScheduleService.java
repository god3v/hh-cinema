package com.cinema.application.service;

import com.cinema.application.dto.MovieScheduleResponseDto;
import com.cinema.application.port.in.MovieScheduleUseCase;
import com.cinema.application.port.out.MovieSchedulePort;
import com.cinema.domain.model.MovieSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MovieScheduleService implements MovieScheduleUseCase {

    private final MovieSchedulePort movieSchedulePort;

    @Override
    public List<MovieScheduleResponseDto> getNowPlayingMovies() {
        List<MovieSchedule> schedules = movieSchedulePort.findNowPlayingMovies();

        Map<Long, MovieScheduleQueryResult> movieMap = new HashMap<>();

        for (MovieSchedule schedule : schedules) {
            Long movieId = schedule.id();

            MovieScheduleQueryResult movieSchedule = movieMap.computeIfAbsent(movieId, id ->
                    MovieScheduleApplicationMapper.toDTO(schedule)
            );

            movieSchedule.addSchedule(MovieScheduleQueryResult.Schedule.builder()
                    .screenName(schedule.screenName())
                    .startedAt(schedule.startedAt())
                    .endedAt(schedule.endedAt())
                    .build());
        }

        return new ArrayList<>(movieMap.values());
    }
}
