package com.cinema.domain.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ScreeningSchedule(
        Long id, // 상영 일정 ID
        LocalDateTime startAt, // 상영 시작 시간
        LocalDateTime endAt, // 상영 종료 시간
        Movie movie, // 영화
        Screen screen // 상영관
) {
}
