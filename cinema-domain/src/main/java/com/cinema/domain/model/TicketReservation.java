package com.cinema.domain.model;

public record TicketReservation(
        Long id, // 영화 예매 ID
        ScreeningSchedule schedule, // 상영 일정
        ScreenSeat seat, // 상영관 좌석
        User user // 회원
) {
}
