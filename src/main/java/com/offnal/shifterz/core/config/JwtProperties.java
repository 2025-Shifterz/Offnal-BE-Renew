package com.offnal.shifterz.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenValiditySecond,
        long refreshTokenValiditySecond
) {
}
