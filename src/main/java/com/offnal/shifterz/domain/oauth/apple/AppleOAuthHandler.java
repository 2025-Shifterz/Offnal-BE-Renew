package com.offnal.shifterz.domain.oauth.apple;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offnal.shifterz.core.config.AppleProperties;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.oauth.LoginRequest;
import com.offnal.shifterz.domain.oauth.OAuthHandler;
import com.offnal.shifterz.domain.oauth.OAuthUserInfo;
import com.offnal.shifterz.domain.oauth.apple.exception.AppleErrorCode;
import com.offnal.shifterz.domain.oauth.exception.OAuthErrorCode;
import com.offnal.shifterz.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleOAuthHandler implements OAuthHandler {
    private final AppleProperties appleProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";
    private static final String APPLE_REVOKE_URL = "https://appleid.apple.com/auth/revoke";

    private final Map<String, PublicKey> cachedKeys = new ConcurrentHashMap<>();
    private long lastFetchTime = 0L;

    @Override
    public Provider getProviderType() {
        return Provider.APPLE;
    }

    @Override
    public OAuthUserInfo getUserInfo(LoginRequest dto) {
        if (!(dto instanceof AppleLoginRequest appleDto)) {
            throw new CustomException(OAuthErrorCode.UNSUPPORTED_PROVIDER);
        }

        DecodedJWT jwt = verifyIdentityToken(dto.getToken());
        AppleAuthTokenResponse token = exchangeAuthorizationCode(appleDto.getAuthorizationCode());

        return OAuthUserInfo.builder()
                .provider(Provider.APPLE)
                .providerId(jwt.getSubject())
                .email(appleDto.getEmail() != null ? appleDto.getEmail() : jwt.getClaim("email").asString())
                .nickname(resolveNickname(appleDto))
                .appleRefreshToken(token.getRefreshToken())
                .build();
    }

    public void revoke(Member member) {
        if (member.getAppleRefreshToken() == null) return;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleProperties.clientId());
        params.add("client_secret", createClientSecret());
        params.add("token", member.getAppleRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<String> response = restTemplate.postForEntity(
                APPLE_REVOKE_URL,
                new HttpEntity<>(params, headers),
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(AppleErrorCode.APPLE_REVOKE_FAIL);
        }
    }

    private DecodedJWT verifyIdentityToken(String identityToken) {
        try {
            DecodedJWT jwt = JWT.decode(identityToken);
            PublicKey publicKey = getApplePublicKey(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .withAudience(appleProperties.clientId())
                    .build();
            return verifier.verify(identityToken);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(AppleErrorCode.APPLE_TOKEN_INVALID);
        }
    }

    private AppleAuthTokenResponse exchangeAuthorizationCode(String authorizationCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleProperties.clientId());
        params.add("client_secret", createClientSecret());
        params.add("grant_type", "authorization_code");
        params.add("code", authorizationCode);
        params.add("redirect_uri", appleProperties.redirectUri());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<String> rawResponse = restTemplate.postForEntity(
                APPLE_TOKEN_URL,
                new HttpEntity<>(params, headers),
                String.class
        );

        if (!rawResponse.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(AppleErrorCode.APPLE_TOKEN_EXCHANGE_FAIL);
        }

        try {
            return objectMapper.readValue(rawResponse.getBody(), AppleAuthTokenResponse.class);
        } catch (Exception e) {
            log.error("Apple Token Parse Error", e);
            throw new CustomException(AppleErrorCode.APPLE_TOKEN_EXCHANGE_FAIL);
        }
    }

    private PublicKey getApplePublicKey(String kid) {
        try {
            if (cachedKeys.containsKey(kid) && System.currentTimeMillis() - lastFetchTime < 3_600_000) {
                return cachedKeys.get(kid);
            }
            JsonNode keys = objectMapper
                    .readTree(restTemplate.getForObject(APPLE_PUBLIC_KEYS_URL, String.class))
                    .get("keys");
            for (JsonNode key : keys) {
                cachedKeys.put(
                        key.get("kid").asText(),
                        createPublicKey(key.get("n").asText(), key.get("e").asText())
                );
            }
            lastFetchTime = System.currentTimeMillis();
            if (!cachedKeys.containsKey(kid)) {
                throw new CustomException(AppleErrorCode.APPLE_PUBLIC_KEY_NOT_FOUND);
            }
            return cachedKeys.get(kid);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(AppleErrorCode.APPLE_PUBLIC_KEY_ERROR);
        }
    }

    private PublicKey createPublicKey(String n, String e) {
        try {
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
            return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
        } catch (Exception ex) {
            throw new CustomException(AppleErrorCode.APPLE_PUBLIC_KEY_ERROR);
        }
    }

    private String createClientSecret() {
        try {
            String keyPath = appleProperties.privateKeyPath();
            String privateKeyPem = Files.readString(Path.of(keyPath))
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);
            PrivateKey privateKey = KeyFactory.getInstance("EC")
                    .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            Algorithm algorithm = Algorithm.ECDSA256(null, (ECPrivateKey) privateKey);

            Instant now = Instant.now();
            return JWT.create()
                    .withKeyId(appleProperties.keyId())
                    .withIssuer(appleProperties.teamId())
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(now.plusSeconds(3600 * 3)))
                    .withAudience("https://appleid.apple.com")
                    .withSubject(appleProperties.clientId())
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("[Apple] createClientSecret FAILED", e);
            throw new CustomException(AppleErrorCode.APPLE_CLIENT_SECRET_ERROR);
        }
    }

    private String resolveNickname(AppleLoginRequest request) {
        if (request.getFullName() != null) {
            String name = request.getFullName().getFullName();
            if (name != null && !name.isBlank()) return name;
        }
        return "Apple User";
    }
}
