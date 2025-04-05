package com.cinema.adapter.out.persistence;

import com.cinema.application.port.out.ReservationRateLimiterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisReservationRateLimiterAdapter implements ReservationRateLimiterPort {

    private final RedisRateLimitHelper helper;

    @Override
    public boolean isBlocked(Long userId, Long scheduleId) {
        return helper.keyExists(buildKey(userId, scheduleId));
    }

    @Override
    public void markReserved(Long userId, Long scheduleId) {
        helper.setKeyWithExpire(buildKey(userId, scheduleId), Duration.ofMinutes(5));
    }

    private String buildKey(Long userId, Long scheduleId) {
        return "reservation_limit:" + userId + ":" + scheduleId;
    }
}
