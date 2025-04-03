package com.cinema.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record ReservationRequest(
        @NotNull(message = "{validation.userId.notnull}")
        @Positive(message = "{validation.id.positive")
        Long userId,
        @NotNull(message = "{validation.scheduleId.notnull}")
        @Positive(message = "{validation.id.positive")
        Long scheduleId,
        @NotNull(message = "{validation.seatIds.notnull}")
        @Size(min = 1, max = 5, message = "{validation.seatIds.size}")
        List<@NotNull(message = "{validation.seatIds.seatId.notnull}")
                @Positive(message = "{validation.id.positive")
                Long> seatIds
) {
}
