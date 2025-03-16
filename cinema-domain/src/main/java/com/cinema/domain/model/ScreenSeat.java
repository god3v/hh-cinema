package com.cinema.domain.model;

public record ScreenSeat(
        Long id, // 상영관 좌석 ID
        Screen screen ,// 상영관
        int row, // 좌석 위치 행
        int col // 좌석 위치 열
) {
}
