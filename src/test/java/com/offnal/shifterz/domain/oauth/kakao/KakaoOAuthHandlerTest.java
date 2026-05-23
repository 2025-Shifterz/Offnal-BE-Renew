package com.offnal.shifterz.domain.oauth.kakao;

import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.oauth.OAuthUserInfoDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class KakaoOAuthHandlerTest {

    private MockWebServer mockWebServer;
    private KakaoOAuthHandler kakaoOAuthHandler;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient = WebClient.create(mockWebServer.url("/").toString());
        kakaoOAuthHandler = new KakaoOAuthHandler(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getProviderType은_KAKAO를_반환한다() {
        assertThat(kakaoOAuthHandler.getProviderType()).isEqualTo(Provider.KAKAO);
    }

    @Test
    void getUserInfo_OAuthUserInfoDto로_정상_변환한다() {
        // given
        mockWebServer.enqueue(kakaoApiResponse(
                12345L, "kakao@test.com", "카카오유저", "https://img.kakao.com/profile.jpg"));

        KakaoLoginRequestDto request = mock(KakaoLoginRequestDto.class);
        given(request.getToken()).willReturn("valid-access-token");

        // when
        OAuthUserInfoDto result = kakaoOAuthHandler.getUserInfo(request);

        // then
        assertThat(result.getProvider()).isEqualTo(Provider.KAKAO);
        assertThat(result.getProviderId()).isEqualTo("12345");
        assertThat(result.getEmail()).isEqualTo("kakao@test.com");
        assertThat(result.getNickname()).isEqualTo("카카오유저");
        assertThat(result.getProfileImageUrl()).isEqualTo("https://img.kakao.com/profile.jpg");
        assertThat(result.getAppleRefreshToken()).isNull();
    }

    @Test
    void getUserInfo_profileImageUrl이_null이어도_정상_변환한다() {
        // given
        mockWebServer.enqueue(kakaoApiResponse(1L, "kakao@test.com", "유저", null));

        KakaoLoginRequestDto request = mock(KakaoLoginRequestDto.class);
        given(request.getToken()).willReturn("valid-access-token");

        // when
        OAuthUserInfoDto result = kakaoOAuthHandler.getUserInfo(request);

        // then
        assertThat(result.getProviderId()).isEqualTo("1");
        assertThat(result.getProfileImageUrl()).isNull();
    }

    private MockResponse kakaoApiResponse(long id, String email, String nickname, String profileImageUrl) {
        String profileImageJson = profileImageUrl != null
                ? "\"profile_image_url\":\"" + profileImageUrl + "\""
                : "\"profile_image_url\":null";

        String body = String.format("""
                {
                  "id": %d,
                  "kakao_account": {
                    "email": "%s",
                    "profile": {
                      "nickname": "%s",
                      %s
                    }
                  }
                }
                """, id, email, nickname, profileImageJson);

        return new MockResponse()
                .setBody(body)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
}