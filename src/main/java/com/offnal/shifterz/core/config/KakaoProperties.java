package com.offnal.shifterz.core.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(
        String clientId,
        String redirectUri,
        Url url
) {
    public record Url(
        String token,
        String user
    ) {}
}
