package com.offnal.shifterz.domain.member.service;

public interface SocialService<T> { // T = 소셜별 DTO
    String getAccessToken(String code);
    T getUserInfo(String accessToken);
}

