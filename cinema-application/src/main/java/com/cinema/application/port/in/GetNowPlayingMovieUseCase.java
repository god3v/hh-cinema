package com.cinema.application.port.in;

import com.cinema.application.dto.MovieSearchResult;
import com.cinema.application.dto.MovieSearchQuery;

import java.util.List;

public interface GetNowPlayingMovieUseCase {

    List<MovieSearchResult> getNowPlayingMovies(MovieSearchQuery request);
}
