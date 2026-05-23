package com.offnal.shifterz.domain.oauth.apple;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offnal.shifterz.core.config.AppleProperties;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.oauth.OAuthUserInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class AppleOAuthHandlerTest {

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";
    private static final String TEST_CLIENT_ID = "com.offnal.test";
    private static final String TEST_KID = "test-kid";

    @TempDir Path tempDir;
    @Mock private AppleProperties appleProperties;

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private AppleOAuthHandler appleOAuthHandler;

    private RSAPublicKey rsaPublicKey;
    private RSAPrivateKey rsaPrivateKey;
    private String ecKeyFilePath;

    @BeforeEach
    void setUp() throws Exception {
        // RSA 키쌍 (identityToken 서명/검증용)
        KeyPairGenerator rsaGen = KeyPairGenerator.getInstance("RSA");
        rsaGen.initialize(2048);
        KeyPair rsaKeyPair = rsaGen.generateKeyPair();
        rsaPublicKey = (RSAPublicKey) rsaKeyPair.getPublic();
        rsaPrivateKey = (RSAPrivateKey) rsaKeyPair.getPrivate();

        // EC 키쌍 (createClientSecret용) - 임시 파일로 저장
        KeyPairGenerator ecGen = KeyPairGenerator.getInstance("EC");
        ecGen.initialize(256);
        KeyPair ecKeyPair = ecGen.generateKeyPair();
        Path ecKeyFile = tempDir.resolve("AuthKey_test.p8");
        String pem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'})
                        .encodeToString(ecKeyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----";
        Files.writeString(ecKeyFile, pem);
        ecKeyFilePath = ecKeyFile.toString();

        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        appleOAuthHandler = new AppleOAuthHandler(appleProperties, new ObjectMapper(), restTemplate);
    }

    // getUserInfo 테스트에서만 호출 - getProviderType 테스트에는 불필요
    private void stubAppleProperties() {
        given(appleProperties.clientId()).willReturn(TEST_CLIENT_ID);
        given(appleProperties.privateKeyPath()).willReturn(ecKeyFilePath);
        given(appleProperties.keyId()).willReturn("test-key-id");
        given(appleProperties.teamId()).willReturn("TEST_TEAM");
        given(appleProperties.redirectUri()).willReturn("https://example.com/callback");
    }

    @Test
    void getProviderType은_APPLE을_반환한다() {
        assertThat(appleOAuthHandler.getProviderType()).isEqualTo(Provider.APPLE);
    }

    @Test
    void fullName이_있으면_nickname으로_사용한다() {
        // given
        stubAppleProperties();
        String identityToken = signedIdentityToken("apple-sub-123", "apple@test.com");
        mockServer.expect(requestTo(APPLE_PUBLIC_KEYS_URL))
                .andRespond(withSuccess(buildJwksJson(), MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(APPLE_TOKEN_URL))
                .andRespond(withSuccess("{\"refresh_token\":\"refresh-token\"}", MediaType.APPLICATION_JSON));

        AppleLoginRequestDto request = mock(AppleLoginRequestDto.class);
        AppleLoginRequestDto.FullName fullName = mock(AppleLoginRequestDto.FullName.class);
        given(request.getToken()).willReturn(identityToken);
        given(request.getAuthorizationCode()).willReturn("auth-code");
        given(request.getEmail()).willReturn("apple@test.com");
        given(request.getFullName()).willReturn(fullName);
        given(fullName.getFullName()).willReturn("홍길동");

        // when
        OAuthUserInfoDto result = appleOAuthHandler.getUserInfo(request);

        // then
        assertThat(result.getProvider()).isEqualTo(Provider.APPLE);
        assertThat(result.getProviderId()).isEqualTo("apple-sub-123");
        assertThat(result.getNickname()).isEqualTo("홍길동");
        assertThat(result.getEmail()).isEqualTo("apple@test.com");
        assertThat(result.getAppleRefreshToken()).isEqualTo("refresh-token");
        mockServer.verify();
    }

    @Test
    void fullName이_null이면_기본값_AppleUser를_사용한다() {
        // given
        stubAppleProperties();
        String identityToken = signedIdentityToken("apple-sub-123", "apple@test.com");
        mockServer.expect(requestTo(APPLE_PUBLIC_KEYS_URL))
                .andRespond(withSuccess(buildJwksJson(), MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(APPLE_TOKEN_URL))
                .andRespond(withSuccess("{\"refresh_token\":\"refresh-token\"}", MediaType.APPLICATION_JSON));

        AppleLoginRequestDto request = mock(AppleLoginRequestDto.class);
        given(request.getToken()).willReturn(identityToken);
        given(request.getAuthorizationCode()).willReturn("auth-code");
        given(request.getEmail()).willReturn("apple@test.com");
        given(request.getFullName()).willReturn(null);

        // when
        OAuthUserInfoDto result = appleOAuthHandler.getUserInfo(request);

        // then
        assertThat(result.getNickname()).isEqualTo("Apple User");
        mockServer.verify();
    }

    @Test
    void request_email이_null이면_identityToken의_email을_사용한다() {
        // given
        stubAppleProperties();
        String identityToken = signedIdentityToken("apple-sub-123", "from-token@test.com");
        mockServer.expect(requestTo(APPLE_PUBLIC_KEYS_URL))
                .andRespond(withSuccess(buildJwksJson(), MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(APPLE_TOKEN_URL))
                .andRespond(withSuccess("{\"refresh_token\":\"refresh-token\"}", MediaType.APPLICATION_JSON));

        AppleLoginRequestDto request = mock(AppleLoginRequestDto.class);
        given(request.getToken()).willReturn(identityToken);
        given(request.getAuthorizationCode()).willReturn("auth-code");
        given(request.getEmail()).willReturn(null);
        given(request.getFullName()).willReturn(null);

        // when
        OAuthUserInfoDto result = appleOAuthHandler.getUserInfo(request);

        // then
        assertThat(result.getEmail()).isEqualTo("from-token@test.com");
        mockServer.verify();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private String signedIdentityToken(String sub, String email) {
        Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
        return JWT.create()
                .withKeyId(TEST_KID)
                .withIssuer("https://appleid.apple.com")
                .withAudience(TEST_CLIENT_ID)
                .withSubject(sub)
                .withClaim("email", email)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                .sign(algorithm);
    }

    private String buildJwksJson() {
        String n = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(stripLeadingZero(rsaPublicKey.getModulus().toByteArray()));
        String e = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(stripLeadingZero(rsaPublicKey.getPublicExponent().toByteArray()));
        return String.format(
                "{\"keys\":[{\"kty\":\"RSA\",\"kid\":\"%s\",\"n\":\"%s\",\"e\":\"%s\"}]}",
                TEST_KID, n, e);
    }

    private byte[] stripLeadingZero(byte[] bytes) {
        if (bytes.length > 1 && bytes[0] == 0x00) {
            byte[] stripped = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, stripped, 0, stripped.length);
            return stripped;
        }
        return bytes;
    }
}