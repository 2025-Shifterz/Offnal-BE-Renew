package com.offnal.shifterz.domain.oauth.kakao;

import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.oauth.OAuthUserInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class KakaoOAuthHandlerTest {
    @Mock
    private KakaoService kakaoService;

    @InjectMocks
    private KakaoOAuthHandler kakaoOAuthHandler;

    @Test
    void getProviderType은_KAKAO를_반환한다() {
        assertThat(kakaoOAuthHandler.getProviderType()).isEqualTo(Provider.KAKAO);
    }

    @Test
    void KakaoUserInfoResponseDto를_OAuthUserInfoDto로_정상_변환한다() {
        // given
        KakaoUserInfoResponseDto dto = mock(KakaoUserInfoResponseDto.class);
        KakaoUserInfoResponseDto.KakaoAccount account = mock(KakaoUserInfoResponseDto.KakaoAccount.class);
        KakaoUserInfoResponseDto.KakaoAccount.Profile profile = mock(KakaoUserInfoResponseDto.KakaoAccount.Profile.class);

        given(dto.getId()).willReturn(12345L);
        given(dto.getKakaoAccount()).willReturn(account);
        given(account.getEmail()).willReturn("kakao@test.com");
        given(account.getProfile()).willReturn(profile);
        given(profile.getNickName()).willReturn("카카오유저");
        given(profile.getProfileImageUrl()).willReturn("https://img.kakao.com/profile.jpg");

        // when
        OAuthUserInfoDto result = kakaoOAuthHandler.toOAuthUserInfoDto(dto);

        // then
        assertThat(result.getProviderId()).isEqualTo("12345");
        assertThat(result.getEmail()).isEqualTo("kakao@test.com");
        assertThat(result.getNickname()).isEqualTo("카카오유저");
        assertThat(result.getProfileImageUrl()).isEqualTo("https://img.kakao.com/profile.jpg");
        assertThat(result.getAppleRefreshToken()).isNull();
    }

    @Test
    void accessToken으로_사용자_정보를_조회하고_변환한다() {
        // given
        String accessToken = "valid-access-token";
        KakaoUserInfoResponseDto dto = mock(KakaoUserInfoResponseDto.class);
        KakaoUserInfoResponseDto.KakaoAccount account = mock(KakaoUserInfoResponseDto.KakaoAccount.class);
        KakaoUserInfoResponseDto.KakaoAccount.Profile profile = mock(KakaoUserInfoResponseDto.KakaoAccount.Profile.class);

        given(kakaoService.getUserInfo(accessToken)).willReturn(dto);
        given(dto.getId()).willReturn(1L);
        given(dto.getKakaoAccount()).willReturn(account);
        given(account.getEmail()).willReturn("kakao@test.com");
        given(account.getProfile()).willReturn(profile);
        given(profile.getNickName()).willReturn("유저");
        given(profile.getProfileImageUrl()).willReturn(null);

        // when
        OAuthUserInfoDto result = kakaoOAuthHandler.getUserInfo(accessToken);

        // then
        assertThat(result.getProviderId()).isEqualTo("1");
        then(kakaoService).should(times(1)).getUserInfo(accessToken);
    }
}