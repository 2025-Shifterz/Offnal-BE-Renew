package com.offnal.shifterz.domain.oauth;

import com.offnal.shifterz.core.jwt.JwtTokenProvider;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.dto.AuthResponseDto;
import com.offnal.shifterz.domain.member.dto.MemberResponseDto;
import com.offnal.shifterz.domain.member.service.MemberService;
import com.offnal.shifterz.domain.oauth.apple.AppleLoginRequest;
import com.offnal.shifterz.domain.oauth.apple.AppleOAuthHandler;
import com.offnal.shifterz.domain.oauth.kakao.KakaoLoginRequest;
import com.offnal.shifterz.domain.oauth.kakao.KakaoOAuthHandler;
import com.offnal.shifterz.global.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock private KakaoOAuthHandler kakaoOAuthHandler;
    @Mock
    private AppleOAuthHandler appleOAuthHandler;
    @Mock private MemberService memberService;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private LoginService loginService;

    @Test
    void 카카오_네이티브_로그인_성공() {
        // given
        KakaoLoginRequest request = mock(KakaoLoginRequest.class);
        given(request.getAccessToken()).willReturn("kakao-access-token");

        OAuthUserInfoDto userInfo = OAuthUserInfoDto.builder()
                .providerId("12345").email("kakao@test.com").nickname("카카오유저").build();

        MemberResponseDto.MemberRegisterResponseDto registerResult =
                MemberResponseDto.MemberRegisterResponseDto.builder()
                        .id(1L).email("kakao@test.com").memberName("카카오유저").isNewMember(true).build();

        given(kakaoOAuthHandler.getUserInfo("kakao-access-token")).willReturn(userInfo);
        given(memberService.registerMemberIfAbsent(any(), any(), any(), any(), any(), any(), any()))
                .willReturn(registerResult);
        given(jwtTokenProvider.createAccessToken(1L)).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(1L)).willReturn("refresh-token");

        // when
        AuthResponseDto result = loginService.loginWithKakaoNative(request);

        // then
        assertThat(result).isNotNull();
        then(memberService).should()
                .registerMemberIfAbsent(eq(Provider.KAKAO), any(), any(), any(), any(), any(), any());
    }

    @Test
    void 유효하지_않은_카카오_토큰이면_CustomException이_발생한다() {
        // given
        KakaoLoginRequest request = mock(KakaoLoginRequest.class);
        given(request.getAccessToken()).willReturn("invalid-token");
        given(kakaoOAuthHandler.getUserInfo("invalid-token")).willThrow(new RuntimeException());

        // when & then
        assertThatThrownBy(() -> loginService.loginWithKakaoNative(request))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void 애플_네이티브_로그인_성공() {
        // given
        AppleLoginRequest request = mock(AppleLoginRequest.class);

        OAuthUserInfoDto userInfo = OAuthUserInfoDto.builder()
                .providerId("apple-sub").email("apple@test.com")
                .nickname("홍길동").appleRefreshToken("apple-refresh-token").build();

        MemberResponseDto.MemberRegisterResponseDto registerResult =
                MemberResponseDto.MemberRegisterResponseDto.builder()
                        .id(2L).email("apple@test.com").memberName("홍길동").isNewMember(false).build();

        given(appleOAuthHandler.getUserInfo(request, null)).willReturn(userInfo);
        given(memberService.registerMemberIfAbsent(any(), any(), any(), any(), any(), any(), any()))
                .willReturn(registerResult);
        given(jwtTokenProvider.createAccessToken(2L)).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(2L)).willReturn("refresh-token");

        // when
        AuthResponseDto result = loginService.loginWithAppleNative(request);

        // then
        assertThat(result).isNotNull();
        then(memberService).should()
                .registerMemberIfAbsent(eq(Provider.APPLE), any(), any(), any(), any(), any(), eq("apple-refresh-token"));
    }
}