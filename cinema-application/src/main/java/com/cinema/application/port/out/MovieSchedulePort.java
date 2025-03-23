package com.cinema.application.port.out;

import com.cinema.domain.model.MovieSchedule;

import java.util.List;

public interface MovieSchedulePort {

    List<MovieSchedule> findNowPlayingMovies(String genre, String title);
}
