package com.offnal.shifterz.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "apple")
public record AppleProperties(
        String teamId,
        String clientId,
        String keyId,
        String privateKeyPath,
        String redirectUri,
        Url url
) {
    public record Url(
            String token,
            String publicKey,
            String revoke
    ) {}
}
