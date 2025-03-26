package com.cinema.adapter.in.web.controller;

import com.cinema.adapter.in.web.dto.mapper.MovieScheduleMapper;
import com.cinema.adapter.in.web.dto.request.MovieScheduleRequest;
import com.cinema.adapter.in.web.dto.response.MovieScheduleResponse;
import com.cinema.application.dto.MovieScheduleQueryResult;
import com.cinema.application.port.in.MovieScheduleUseCase;
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

    private final MovieScheduleUseCase movieScheduleUseCase;

    /**
     * 상영 중인 모든 영화 조회 API
     */
    @GetMapping
    public ResponseEntity<List<MovieScheduleResponse>> getNowPlayingMovies(@Valid @ModelAttribute MovieScheduleRequest request) {
        List<MovieScheduleQueryResult> result = movieScheduleUseCase.getNowPlayingMovies(MovieScheduleMapper.toQuery(request));
        return ResponseEntity.ok(MovieScheduleMapper.toResponseList(result));
    }
}
