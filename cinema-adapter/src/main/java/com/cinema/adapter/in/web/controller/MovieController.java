package com.cinema.adapter.in.web.controller;

import com.cinema.application.dto.MovieScheduleResponseDto;
import com.cinema.application.port.in.MovieScheduleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieScheduleUseCase movieScheduleUseCase;

    /**
     * 상영 중인 모든 영화 조회 API
     */
    @GetMapping
    public ResponseEntity<List<MovieScheduleResponseDto>> getNowPlayingMovies() {
        List<MovieScheduleResponseDto> result = movieScheduleUseCase.getNowPlayingMovies();
        return ResponseEntity.ok(result);
    }
}
