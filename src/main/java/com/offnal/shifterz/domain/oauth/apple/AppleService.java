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
import com.offnal.shifterz.domain.member.service.AppleSocialService;
import com.offnal.shifterz.domain.oauth.OAuthProvider;
import com.offnal.shifterz.domain.oauth.OAuthUserInfoDto;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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
@Service
@RequiredArgsConstructor
public class AppleService implements AppleSocialService, OAuthProvider {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AppleProperties appleProperties;
    private final ResourceLoader resourceLoader;

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";
    private static final String APPLE_REVOKE_URL = "https://appleid.apple.com/auth/revoke";

    private Map<String, PublicKey> cachedKeys = new ConcurrentHashMap<>();
    private long lastFetchTime = 0L;




    @Override
    public AppleUserInfoResponseDto getUserInfoFromIdentityToken(AppleLoginRequest request) {

        DecodedJWT jwt = verifyIdentityToken(request.getIdentityToken());

        String appleUserId = jwt.getSubject();
        String email = jwt.getClaim("email").asString();

        return new AppleUserInfoResponseDto(appleUserId, email);
    }

    @Override
    public DecodedJWT verifyIdentityToken(String identityToken) {

        try {
            DecodedJWT jwt = JWT.decode(identityToken);
            String kid = jwt.getKeyId();

            PublicKey publicKey = getApplePublicKey(kid);

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .withAudience(appleProperties.clientId())
                    .build();

            return verifier.verify(identityToken);

        } catch (Exception e) {
            throw new CustomException(AppleErrorCode.APPLE_TOKEN_INVALID);
        }
    }


    private PublicKey getApplePublicKey(String kid) {
        try {
            if (cachedKeys.containsKey(kid) && System.currentTimeMillis() - lastFetchTime < 3600000) {
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

        } catch (Exception ex) {
            throw new CustomException(AppleErrorCode.APPLE_PUBLIC_KEY_ERROR);
        }
    }

    private PublicKey createPublicKey(String n, String e) {
        try {
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));

            return KeyFactory.getInstance("RSA")
                    .generatePublic(new RSAPublicKeySpec(modulus, exponent));

        } catch (Exception ex) {
            throw new CustomException(AppleErrorCode.APPLE_PUBLIC_KEY_ERROR);
        }
    }



    public AppleAuthTokenResponse exchangeAuthorizationCode(String authorizationCode) {

        String clientSecret = createClientSecret();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleProperties.clientId());
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        params.add("code", authorizationCode);
        params.add("redirect_uri", appleProperties.redirectUri());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<String> rawResponse =
                restTemplate.postForEntity(
                        APPLE_TOKEN_URL,
                        new HttpEntity<>(params, headers),
                        String.class
                );


        if (!rawResponse.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(AppleErrorCode.APPLE_TOKEN_EXCHANGE_FAIL);
        }

        // JSON → DTO 매핑
        try {
            AppleAuthTokenResponse token = objectMapper.readValue(
                    rawResponse.getBody(),
                    AppleAuthTokenResponse.class
            );

            return token;

        } catch (Exception e) {
            log.error("Apple Token Parse Error", e);
            throw new CustomException(AppleErrorCode.APPLE_TOKEN_EXCHANGE_FAIL);
        }
    }



    private String createClientSecret() {

        try {

            String keyPath = appleProperties.privateKeyPath();

            String privateKeyPem = Files.readString(Path.of(keyPath));

            privateKeyPem = privateKeyPem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

            PrivateKey privateKey = KeyFactory.getInstance("EC").generatePrivate(keySpec);
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


    public void revoke(Member member) {

        if (member.getAppleRefreshToken() == null) {
            return;
        }

        String clientSecret = createClientSecret();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleProperties.clientId());
        params.add("client_secret", clientSecret);
        params.add("token", member.getAppleRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        APPLE_REVOKE_URL,
                        new HttpEntity<>(params, headers),
                        String.class
                );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(AppleErrorCode.APPLE_REVOKE_FAIL);
        }
    }

    @Override
    public Provider getProviderType() {
        return Provider.APPLE;
    }

    @Override
    public OAuthUserInfoDto toOAuthUserInfoDto(Object rawUserInfoDto) {
        AppleUserInfoResponseDto dto = (AppleUserInfoResponseDto) rawUserInfoDto;
        return OAuthUserInfoDto.builder()
                .providerId(dto.getSub())
                .email(dto.getEmail())
                .build();
    }

    public OAuthUserInfoDto toOAuthUserInfo(AppleUserInfoResponseDto dto, String appleRefreshToken) {
        return OAuthUserInfoDto.builder()
                .providerId(dto.getSub())
                .email(dto.getEmail())
                .appleRefreshToken(appleRefreshToken)
                .build();
    }

    @Getter
    @AllArgsConstructor
    public enum AppleErrorCode implements ErrorReason {

        APPLE_TOKEN_INVALID("APL001", HttpStatus.UNAUTHORIZED, "유효하지 않은 Apple identity token입니다."),
        APPLE_PUBLIC_KEY_NOT_FOUND("APL002", HttpStatus.INTERNAL_SERVER_ERROR, "kid에 해당하는 Apple 공개키를 찾을 수 없습니다."),
        APPLE_PUBLIC_KEY_ERROR("APL003", HttpStatus.INTERNAL_SERVER_ERROR, "Apple 공개키 처리 중 오류가 발생했습니다."),
        APPLE_TOKEN_EXCHANGE_FAIL("APL004", HttpStatus.BAD_GATEWAY, "authorization_code 토큰 교환 실패"),
        APPLE_CLIENT_SECRET_ERROR("APL005", HttpStatus.INTERNAL_SERVER_ERROR, "client secret 생성 실패"),
        APPLE_REVOKE_FAIL("APL006", HttpStatus.BAD_GATEWAY, "Apple 토큰 revoke 실패");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}
