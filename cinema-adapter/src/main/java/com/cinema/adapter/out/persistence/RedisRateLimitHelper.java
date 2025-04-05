package com.cinema.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class RedisRateLimitHelper {

    private final StringRedisTemplate redisTemplate;

    public boolean runLua(Resource luaScript, String key, String... args) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();

        script.setScriptSource(new ResourceScriptSource(luaScript));
        script.setResultType(Long.class);
        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                (Object[]) args);
        return result != null && result == 1L;
    }

    public void setKeyWithExpire(String key, Duration ttl) {
        redisTemplate.opsForValue().set(key, "1", ttl);
    }

    public boolean keyExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
