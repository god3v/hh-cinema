package com.cinema.common.model;

import lombok.Builder;

@Builder
public record ScreenSeat(
        Long id, // 상영관 좌석 ID
        Long screenId, // 상영관
        char row, // 좌석 위치 행
        int col // 좌석 위치 열
) {
}
