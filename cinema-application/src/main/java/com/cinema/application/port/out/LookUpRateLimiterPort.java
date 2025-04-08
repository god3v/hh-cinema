package com.cinema.application.port.out;

public interface LookUpRateLimiterPort {
    boolean isBlocked(String ip);
    boolean tryAcquire(String ip);
    void markBlocked(String ip);
}
