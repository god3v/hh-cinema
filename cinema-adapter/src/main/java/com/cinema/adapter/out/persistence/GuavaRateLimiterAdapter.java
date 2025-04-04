package com.cinema.adapter.out.persistence;

import com.cinema.application.port.out.RateLimiterPort;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GuavaRateLimiterAdapter implements RateLimiterPort {

    private static final double PERMITS_PER_SECOND = 50.0 / 60.0;
    private final ConcurrentHashMap<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();
    private final Cache<String, Boolean> blockedIpCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS) // 차단 유지 시간
            .maximumSize(1000)
            .build();

    @Override
    public boolean isBlocked(String ip) {
        return blockedIpCache.getIfPresent(ip) != null;
    }

    @Override
    public void markBlocked(String ip) {
        blockedIpCache.put(ip, true);
    }

    @Override
    public boolean tryAcquire(String ip) {
        return rateLimiters
                .computeIfAbsent(ip, key -> RateLimiter.create(PERMITS_PER_SECOND))
                .tryAcquire();
    }
}
