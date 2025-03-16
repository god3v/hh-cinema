package com.cinema.application.port.in;

import com.cinema.application.dto.MovieScheduleResponseDto;

import java.util.List;

public interface MovieScheduleUseCase {

    List<MovieScheduleResponseDto> getNowPlayingMovies();
}
