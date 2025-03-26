package com.cinema.application.dto;

import lombok.Builder;

@Builder
public record MovieScheduleQuery(
        String title,
        String genre
) {
}
