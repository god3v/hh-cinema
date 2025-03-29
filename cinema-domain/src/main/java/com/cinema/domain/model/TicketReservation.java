package com.cinema.common.model;

import lombok.Builder;

import java.util.Comparator;
import java.util.List;

@Builder
public record TicketReservation(
        Long id, // 예매 번호
        ScreeningSchedule schedule, // 상영 일정
        List<ScreenSeat> seats, // 상영관 좌석
        Long userId // 회원 ID
) {

    public boolean checkSeatsContinuous(List<ScreenSeat> seats) {
        List<ScreenSeat> sortedSeats = seats.stream()
                .sorted(Comparator.comparing(ScreenSeat::row).thenComparing(ScreenSeat::col))
                .toList();

        char baseRow = sortedSeats.getFirst().row();
        int previousCol = sortedSeats.getFirst().col();

        for (int i = 1; i < sortedSeats.size(); i++) {
            ScreenSeat currentSeat = sortedSeats.get(i);

            // Row가 바뀌면 연속적이지 않음
            if (currentSeat.row() != baseRow) {
                return false;
            }

            // Col이 연속적이지 않으면 연속성이 깨짐
            if (currentSeat.col() != previousCol + 1) {
                return false;
            }

            // 이전 Col을 현재 Col로 업데이트
            previousCol = currentSeat.col();
        }
        return true;
    }
}
