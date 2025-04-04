package com.cinema.application.port.out;

public interface RateLimiterPort {
    boolean isBlocked(String ip);
    void markBlocked(String ip);
    boolean tryAcquire(String ip);
}
