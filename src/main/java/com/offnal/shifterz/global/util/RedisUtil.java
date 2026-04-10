package com.offnal.shifterz.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;

    public void set(String key, String value, long duration, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, duration, unit);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void setBlackList(String accessToken, long duration, TimeUnit unit) {
        redisTemplate.opsForValue().set("bl:" + accessToken, "logout", duration, unit);
    }

    public boolean isBlackListed(String accessToken) {
        return redisTemplate.hasKey("bl:" + accessToken);
    }
}
