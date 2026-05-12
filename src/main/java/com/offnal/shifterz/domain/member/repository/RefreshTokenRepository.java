package com.offnal.shifterz.domain.member.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(Long memberId, String refreshToken, long duration, TimeUnit unit) {
        redisTemplate.opsForValue().set("rt:" + memberId, refreshToken, duration, unit);
    }


    public String findByMemberId(Long memberId) {
        return redisTemplate.opsForValue().get("rt:" + memberId);
    }


    public void delete(Long memberId) {
        redisTemplate.delete("rt:" + memberId);
    }
}
