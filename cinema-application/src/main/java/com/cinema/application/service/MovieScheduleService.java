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

        Map<Long, MovieScheduleResponseDto> movieMap = new HashMap<>();

        for (MovieSchedule schedule : schedules) {
            Long movieId = schedule.id();

            MovieScheduleResponseDto movieSchedule = movieMap.computeIfAbsent(movieId, id -> MovieScheduleResponseDto.builder()
                    .movieId(schedule.id())
                    .title(schedule.title())
                    .rating(schedule.movieRating().getDescription())
                    .releaseDate(schedule.releaseDate())
                    .thumbnailUrl(schedule.thumbnailUrl())
                    .runningTime(schedule.runningTime())
                    .genre(schedule.genre().getDescription())
                    .schedules(new ArrayList<>())
                    .build()
            );

            movieSchedule.addSchedule(MovieScheduleResponseDto.Schedule.builder()
                    .screenName(schedule.screenName())
                    .startAt(schedule.startAt())
                    .endAt(schedule.endAt())
                    .build());
        }

        return new ArrayList<>(movieMap.values());
    }
}
