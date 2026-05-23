package com.offnal.shifterz.domain.oauth;

import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.oauth.apple.AppleOAuthHandler;
import com.offnal.shifterz.domain.oauth.kakao.KakaoOAuthHandler;
import com.offnal.shifterz.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@ExtendWith(MockitoExtension.class)
class OAuthHandlerFactoryTest {
    private OAuthHandlerFactory factory;

    @Mock private KakaoOAuthHandler kakaoOAuthHandler;
    @Mock private AppleOAuthHandler appleOAuthHandler;

    @BeforeEach
    void setUp() {
        given(kakaoOAuthHandler.getProviderType()).willReturn(Provider.KAKAO);
        given(appleOAuthHandler.getProviderType()).willReturn(Provider.APPLE);
        factory = new OAuthHandlerFactory(List.of(kakaoOAuthHandler, appleOAuthHandler));
    }

    @Test
    void KAKAO_provider로_KakaoOAuthHandler를_반환한다() {
        assertThat(factory.getProvider(Provider.KAKAO)).isEqualTo(kakaoOAuthHandler);
    }

    @Test
    void APPLE_provider로_AppleOAuthHandler를_반환한다() {
        assertThat(factory.getProvider(Provider.APPLE)).isEqualTo(appleOAuthHandler);
    }

    @Test
    void 지원하지_않는_provider_요청시_CustomException이_발생한다() {
        assertThatThrownBy(() -> factory.getProvider(null))
                .isInstanceOf(CustomException.class);
    }
}