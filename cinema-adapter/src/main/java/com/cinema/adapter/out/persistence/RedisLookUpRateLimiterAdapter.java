package com.cinema.adapter.out.persistence;

import com.cinema.application.port.out.LookUpRateLimiterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisLookUpRateLimiterAdapter implements LookUpRateLimiterPort {

    private final RedisRateLimitHelper helper;

    @Value("classpath:scripts/rate_limit_lookup.lua")
    private Resource luaScript;

    private static final Duration BLOCK_TTL = Duration.ofHours(1);


    @Override
    public boolean isBlocked(String ip) {
        return helper.keyExists("blocked_ip:" + ip);
    }

    @Override
    public boolean tryAcquire(String ip) {
        return helper.runLua(luaScript, "rate_limit:" + ip, "1", "5"); // 60초, 50회
    }

    @Override
    public void markBlocked(String ip) {
        helper.setKeyWithExpire("blocked_ip:" + ip, BLOCK_TTL);
    }
}
