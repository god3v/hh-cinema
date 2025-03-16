package com.cinema.domain.model;

import java.time.LocalDateTime;

public record ScreeningSchedule(
        Long id, // 상영 일정 ID
        LocalDateTime start_at, // 상영 시작 시간
        LocalDateTime end_at, // 상영 종료 시간
        Movie movie, // 영화
        Screen screen // 상영관
) {
}
