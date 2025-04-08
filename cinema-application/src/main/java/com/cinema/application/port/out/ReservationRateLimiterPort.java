package com.cinema.application.port.out;

public interface ReservationRateLimiterPort {
    boolean isBlocked(Long userId, Long scheduleId);
    void markReserved(Long userId, Long scheduleId);
}
