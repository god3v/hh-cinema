package com.cinema.application.port.in;

import com.cinema.application.dto.MovieScheduleQueryResult;
import com.cinema.application.dto.MovieScheduleQuery;

import java.util.List;

public interface MovieScheduleUseCase {

    List<MovieScheduleQueryResult> getNowPlayingMovies(MovieScheduleQuery request);
}
