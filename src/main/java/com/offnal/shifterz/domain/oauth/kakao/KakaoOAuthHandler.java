package com.offnal.shifterz.domain.oauth.kakao;

import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.oauth.OAuthHandler;
import com.offnal.shifterz.domain.oauth.OAuthUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuthHandler implements OAuthHandler {
    private final KakaoService kakaoService;

    @Override
    public Provider getProviderType() {
        return Provider.KAKAO;
    }

    @Override
    public OAuthUserInfoDto toOAuthUserInfoDto(Object rawUserInfoDto) {
        KakaoUserInfoResponseDto dto = (KakaoUserInfoResponseDto) rawUserInfoDto;
        return OAuthUserInfoDto.builder()
                .providerId(String.valueOf(dto.getId()))
                .email(dto.getKakaoAccount().getEmail())
                .nickname(dto.getKakaoAccount().getProfile().getNickName())
                .profileImageUrl(dto.getKakaoAccount().getProfile().getProfileImageUrl())
                .build();
    }

    public OAuthUserInfoDto getUserInfo(String accessToken) {
        return toOAuthUserInfoDto(kakaoService.getUserInfo(accessToken));
    }

}
