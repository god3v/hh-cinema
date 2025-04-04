package com.cinema.application.dto;

import lombok.Builder;

@Builder
public record MovieSearchQuery(
        String title,
        String genre
) {
}
