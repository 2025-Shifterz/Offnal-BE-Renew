package com.offnal.shifterz.domain.oauth.kakao;

import com.offnal.shifterz.core.config.KakaoProperties;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.service.SocialService;
import com.offnal.shifterz.domain.oauth.OAuthHandler;
import com.offnal.shifterz.domain.oauth.OAuthUserInfoDto;
import com.offnal.shifterz.domain.oauth.TokenResponseDto;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService implements SocialService<KakaoUserInfoResponseDto> {
    private final KakaoProperties kakaoProperties;

    @Override
    public String getAccessToken(String code) {

        TokenResponseDto kakaoTokenResponseDto = WebClient.create(kakaoProperties.url().token()).post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoProperties.clientId())
                        .queryParam("redirect_uri", kakaoProperties.redirectUri())
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                // 4xx, 5xx 예외처리
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(TokenResponseDto.class)
                .block();


        return kakaoTokenResponseDto.getAccessToken();
    }

    @Override
    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto userInfo = WebClient.create(kakaoProperties.url().user())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .retrieve()
                // 4xx, 5xx 예외처리
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        return userInfo;
    }
}
