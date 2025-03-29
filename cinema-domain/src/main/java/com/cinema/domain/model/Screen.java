package com.cinema.common.model;

import lombok.Builder;

import java.util.List;

@Builder
public record Screen(
        Long id, // 상영관 ID
        String name, // 상영관 이름
        List<ScreenSeat> seats // 좌석
) {
}
