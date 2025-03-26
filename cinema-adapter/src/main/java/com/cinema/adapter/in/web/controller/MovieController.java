package com.cinema.adapter.in.web.controller;

import com.cinema.adapter.in.web.dto.mapper.MovieSearchMapper;
import com.cinema.adapter.in.web.dto.request.GetNowPlayingMovieRequest;
import com.cinema.adapter.in.web.dto.response.GetNowPlayingMovieResponse;
import com.cinema.application.dto.MovieSearchResult;
import com.cinema.application.port.in.GetNowPlayingMovieUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final GetNowPlayingMovieUseCase getNowPlayingMovieUseCase;

    /**
     * 상영 중인 모든 영화 조회 API
     */
    @GetMapping
    public ResponseEntity<List<GetNowPlayingMovieResponse>> getNowPlayingMovies(@Valid @ModelAttribute GetNowPlayingMovieRequest request) {
        List<MovieSearchResult> result = getNowPlayingMovieUseCase.getNowPlayingMovies(MovieSearchMapper.toQuery(request));
        return ResponseEntity.ok(MovieSearchMapper.toResponseList(result));
    }
}
