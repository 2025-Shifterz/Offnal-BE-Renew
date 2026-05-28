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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock private OAuthHandlerFactory oAuthHandlerFactory;
    @Mock private KakaoOAuthHandler kakaoOAuthHandler;
    @Mock private AppleOAuthHandler appleOAuthHandler;
    @Mock private MemberService memberService;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private LoginService loginService;

    @Test
    void 카카오_로그인_성공() {
        // given
        KakaoLoginRequest request = mock(KakaoLoginRequest.class);
        given(request.getProvider()).willReturn(Provider.KAKAO);

        OAuthUserInfo userInfo = OAuthUserInfo.builder()
                .provider(Provider.KAKAO)
                .providerId("12345").email("kakao@test.com").nickname("카카오유저").build();

        MemberResponseDto.MemberRegisterResponseDto registerResult =
                MemberResponseDto.MemberRegisterResponseDto.builder()
                        .id(1L).email("kakao@test.com").memberName("카카오유저").isNewMember(true).build();

        given(oAuthHandlerFactory.getProvider(Provider.KAKAO)).willReturn(kakaoOAuthHandler);
        given(kakaoOAuthHandler.getUserInfo(request)).willReturn(userInfo);
        given(memberService.registerMemberIfAbsent(any(), any(), any(), any(), any(), any(), any()))
                .willReturn(registerResult);
        given(jwtTokenProvider.createAccessToken(1L)).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(1L)).willReturn("refresh-token");

        // when
        AuthResponseDto result = loginService.login(request);

        // then
        assertThat(result).isNotNull();
        then(memberService).should()
                .registerMemberIfAbsent(eq(Provider.KAKAO), any(), any(), any(), any(), any(), any());
    }

    @Test
    void 애플_로그인_성공() {
        // given
        AppleLoginRequest request = mock(AppleLoginRequest.class);
        given(request.getProvider()).willReturn(Provider.APPLE);

        OAuthUserInfo userInfo = OAuthUserInfo.builder()
                .provider(Provider.APPLE)
                .providerId("apple-sub").email("apple@test.com")
                .nickname("홍길동").appleRefreshToken("apple-refresh-token").build();

        MemberResponseDto.MemberRegisterResponseDto registerResult =
                MemberResponseDto.MemberRegisterResponseDto.builder()
                        .id(2L).email("apple@test.com").memberName("홍길동").isNewMember(false).build();

        given(oAuthHandlerFactory.getProvider(Provider.APPLE)).willReturn(appleOAuthHandler);
        given(appleOAuthHandler.getUserInfo(request)).willReturn(userInfo);
        given(memberService.registerMemberIfAbsent(any(), any(), any(), any(), any(), any(), any()))
                .willReturn(registerResult);
        given(jwtTokenProvider.createAccessToken(2L)).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(2L)).willReturn("refresh-token");

        // when
        AuthResponseDto result = loginService.login(request);

        // then
        assertThat(result).isNotNull();
        then(memberService).should()
                .registerMemberIfAbsent(eq(Provider.APPLE), any(), any(), any(), any(), any(), eq("apple-refresh-token"));
    }

    @Test
    void handler에서_예외가_발생하면_그대로_전파된다() {
        // given
        KakaoLoginRequest request = mock(KakaoLoginRequest.class);
        given(request.getProvider()).willReturn(Provider.KAKAO);
        given(oAuthHandlerFactory.getProvider(Provider.KAKAO)).willReturn(kakaoOAuthHandler);
        given(kakaoOAuthHandler.getUserInfo(request)).willThrow(new RuntimeException("Invalid Parameter"));

        // when & then
        assertThatThrownBy(() -> loginService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid Parameter");
    }

    @Test
    void memberId가_null이면_CustomException이_발생한다() {
        // given
        KakaoLoginRequest request = mock(KakaoLoginRequest.class);
        given(request.getProvider()).willReturn(Provider.KAKAO);

        OAuthUserInfo userInfo = OAuthUserInfo.builder()
                .provider(Provider.KAKAO).providerId("12345").email("kakao@test.com").build();

        MemberResponseDto.MemberRegisterResponseDto registerResult =
                MemberResponseDto.MemberRegisterResponseDto.builder()
                        .id(null).email("kakao@test.com").memberName("카카오유저").isNewMember(true).build();

        given(oAuthHandlerFactory.getProvider(Provider.KAKAO)).willReturn(kakaoOAuthHandler);
        given(kakaoOAuthHandler.getUserInfo(request)).willReturn(userInfo);
        given(memberService.registerMemberIfAbsent(any(), any(), any(), any(), any(), any(), any()))
                .willReturn(registerResult);

        // when & then
        assertThatThrownBy(() -> loginService.login(request))
                .isInstanceOf(com.offnal.shifterz.global.exception.CustomException.class);
    }
}