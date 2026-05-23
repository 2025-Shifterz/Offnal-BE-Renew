package com.offnal.shifterz.domain.oauth.kakao;

import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.oauth.LoginRequestDto;
import com.offnal.shifterz.domain.oauth.OAuthHandler;
import com.offnal.shifterz.domain.oauth.OAuthUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class KakaoOAuthHandler implements OAuthHandler {

    private final WebClient kakaoWebClient;

    public KakaoOAuthHandler(@Qualifier("kakaoWebClient") WebClient kakaoWebClient) {
        this.kakaoWebClient = kakaoWebClient;
    }

    @Override
    public Provider getProviderType() {
        return Provider.KAKAO;
    }

    @Override
    public OAuthUserInfoDto getUserInfo(LoginRequestDto dto) {
        KakaoUserInfoResponseDto rawInfo = fetchUserInfo(dto.getToken());
        return OAuthUserInfoDto.builder()
                .provider(Provider.KAKAO)
                .providerId(String.valueOf(rawInfo.getId()))
                .email(rawInfo.getKakaoAccount().getEmail())
                .nickname(rawInfo.getKakaoAccount().getProfile().getNickName())
                .profileImageUrl(rawInfo.getKakaoAccount().getProfile().getProfileImageUrl())
                .build();
    }

    private KakaoUserInfoResponseDto fetchUserInfo(String accessToken) {
        return kakaoWebClient
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, r -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }
}