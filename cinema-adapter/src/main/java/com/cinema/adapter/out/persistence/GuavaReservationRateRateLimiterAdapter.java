package com.cinema.adapter.out.persistence;

import com.cinema.application.port.out.ReservationRateLimiterPort;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class GuavaReservationRateRateLimiterAdapter implements ReservationRateLimiterPort {

    private final Cache<Object, Object> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    private String buildKey(Long userId, Long scheduleId) {
        return userId + ":" + scheduleId;
    }

    @Override
    public boolean isBlocked(Long userId, Long scheduleId) {
        return cache.getIfPresent(buildKey(userId, scheduleId)) != null;
    }

    @Override
    public void markReserved(Long userId, Long scheduleId) {
        cache.put(buildKey(userId, scheduleId), true);
    }
}
