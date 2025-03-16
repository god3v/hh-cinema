package com.cinema.application.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MovieScheduleResponseDto(
        Long movieId,
        String title,
        String rating,
        LocalDate releaseDate,
        String thumbnailUrl,
        int runningTime,
        String genre,
        List<Schedule> schedules
) {
    public void addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
    }

    @Builder
    public record Schedule (
            String screenName,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
    }
}
