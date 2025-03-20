package com.cinema.adapter.in.web.dto.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
        String message,
        Object payload
) {

    public static ErrorResponse of(String message, Object errors) {
        return ErrorResponse.builder()
                .message(message)
                .payload(errors)
                .build();
    }
}
