package com.cinema.adapter.in.web.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationResponse(
        Long reservationId, // 예매 번호
        Long movieId, // 영화 ID
        String title, // 영화 제목
        Long screenId, // 상영관 ID
        String screenName, // 상영관 이름
        Long scheduleId, // 상영 일정 ID
        LocalDateTime startedAt, // 상영 시작 시간
        LocalDateTime endedAt, // 상영 종료 시간
        Long seatId, // 좌석 ID
        char row, // 좌석 행
        int col // 좌석 열
) {
}
