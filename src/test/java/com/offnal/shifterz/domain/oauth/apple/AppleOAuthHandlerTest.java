package com.offnal.shifterz.domain.oauth.apple;

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
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AppleOAuthHandlerTest {
    @Mock
    private AppleService appleService;

    @InjectMocks
    private AppleOAuthHandler appleOAuthHandler;

    @Test
    void getProviderType은_APPLE을_반환한다() {
        assertThat(appleOAuthHandler.getProviderType()).isEqualTo(Provider.APPLE);
    }

    @Test
    void fullName이_있으면_nickname으로_사용한다() {
        // given
        AppleLoginRequest request = mock(AppleLoginRequest.class);
        AppleLoginRequest.FullName fullName = mock(AppleLoginRequest.FullName.class);
        AppleUserInfoResponseDto rawInfo = new AppleUserInfoResponseDto("apple-sub-123", "apple@test.com");
        AppleAuthTokenResponse token = mock(AppleAuthTokenResponse.class);

        given(request.getAuthorizationCode()).willReturn("auth-code");
        given(request.getEmail()).willReturn("apple@test.com");
        given(request.getFullName()).willReturn(fullName);
        given(fullName.getFullName()).willReturn("홍길동");
        given(appleService.getUserInfoFromIdentityToken(request)).willReturn(rawInfo);
        given(appleService.exchangeAuthorizationCode("auth-code")).willReturn(token);
        given(token.getRefreshToken()).willReturn("refresh-token");

        // when
        OAuthUserInfoDto result = appleOAuthHandler.getUserInfo(request, null);

        // then
        assertThat(result.getNickname()).isEqualTo("홍길동");
        assertThat(result.getAppleRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void fullName이_null이면_기본값_AppleUser를_사용한다() {
        // given
        AppleLoginRequest request = mock(AppleLoginRequest.class);
        AppleUserInfoResponseDto rawInfo = new AppleUserInfoResponseDto("apple-sub-123", "apple@test.com");
        AppleAuthTokenResponse token = mock(AppleAuthTokenResponse.class);

        given(request.getAuthorizationCode()).willReturn("auth-code");
        given(request.getEmail()).willReturn("apple@test.com");
        given(request.getFullName()).willReturn(null);
        given(appleService.getUserInfoFromIdentityToken(request)).willReturn(rawInfo);
        given(appleService.exchangeAuthorizationCode("auth-code")).willReturn(token);
        given(token.getRefreshToken()).willReturn("refresh-token");

        // when
        OAuthUserInfoDto result = appleOAuthHandler.getUserInfo(request, null);

        // then
        assertThat(result.getNickname()).isEqualTo("Apple User");
    }

    @Test
    void request_email이_null이면_identityToken의_email을_사용한다() {
        // given
        AppleLoginRequest request = mock(AppleLoginRequest.class);
        AppleUserInfoResponseDto rawInfo = new AppleUserInfoResponseDto("apple-sub-123", "from-token@test.com");
        AppleAuthTokenResponse token = mock(AppleAuthTokenResponse.class);

        given(request.getAuthorizationCode()).willReturn("auth-code");
        given(request.getEmail()).willReturn(null);
        given(request.getFullName()).willReturn(null);
        given(appleService.getUserInfoFromIdentityToken(request)).willReturn(rawInfo);
        given(appleService.exchangeAuthorizationCode("auth-code")).willReturn(token);
        given(token.getRefreshToken()).willReturn("refresh-token");

        // when
        OAuthUserInfoDto result = appleOAuthHandler.getUserInfo(request, null);

        // then
        assertThat(result.getEmail()).isEqualTo("from-token@test.com");
    }
}