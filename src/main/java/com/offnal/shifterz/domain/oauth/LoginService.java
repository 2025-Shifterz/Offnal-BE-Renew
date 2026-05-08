package com.offnal.shifterz.domain.oauth;

import com.offnal.shifterz.core.jwt.JwtTokenProvider;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.dto.AuthResponseDto;
import com.offnal.shifterz.domain.member.dto.MemberResponseDto;
import com.offnal.shifterz.domain.member.service.MemberService;
import com.offnal.shifterz.domain.oauth.apple.AppleAuthTokenResponse;
import com.offnal.shifterz.domain.oauth.apple.AppleLoginRequest;
import com.offnal.shifterz.domain.oauth.apple.AppleService;
import com.offnal.shifterz.domain.oauth.apple.AppleUserInfoResponseDto;
import com.offnal.shifterz.domain.oauth.kakao.KakaoLoginRequest;
import com.offnal.shifterz.domain.oauth.kakao.KakaoService;
import com.offnal.shifterz.domain.oauth.kakao.KakaoUserInfoResponseDto;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final KakaoService kakaoService;
    private final AppleService appleService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthProviderRegistry providerRegistry;

    public AuthResponseDto loginWithAppleNative(AppleLoginRequest request) {

        AppleUserInfoResponseDto rawInfo = appleService.getUserInfoFromIdentityToken(request);

        AppleAuthTokenResponse appleToken =
                appleService.exchangeAuthorizationCode(request.getAuthorizationCode());

        OAuthUserInfoDto userInfo = appleService.toOAuthUserInfoDto(request, rawInfo, appleToken.getRefreshToken());

        return processLogin(Provider.APPLE, userInfo);
    }

    private AuthResponseDto processLogin(Provider provider, OAuthUserInfoDto userInfo){
        MemberResponseDto.MemberRegisterResponseDto result = memberService.registerMemberIfAbsent(
                provider,
                userInfo.getProviderId(),
                userInfo.getEmail(),
                userInfo.getNickname(),
                null,
                userInfo.getProfileImageUrl(),
                userInfo.getAppleRefreshToken()
        );
        return issueTokens(result);
    }


    private AuthResponseDto issueTokens(MemberResponseDto.MemberRegisterResponseDto result) {
        if (result.getId() == null) {
            throw new CustomException(MemberService.MemberErrorCode.MEMBER_SAVE_FAILED);
        }

        String jwtAccessToken = jwtTokenProvider.createToken(result.getId());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(result.getId());

        return AuthResponseDto.from(result, jwtAccessToken, jwtRefreshToken);
    }


    public AuthResponseDto loginWithKakaoNative(KakaoLoginRequest request) {
        try {
            KakaoUserInfoResponseDto rawInfo = kakaoService.getUserInfo(request.getAccessToken());
            OAuthUserInfoDto userInfo = kakaoService.toOAuthUserInfoDto(rawInfo);
            return processLogin(Provider.KAKAO, userInfo);
        } catch (Exception e) {
            throw new CustomException(LoginErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }

    @Getter
    @AllArgsConstructor
    private enum LoginErrorCode implements ErrorReason {
        INVALID_SOCIAL_TOKEN("AUTH002", HttpStatus.UNAUTHORIZED, "유효하지 않은 소셜 액세스 토큰입니다."),
        SOCIAL_USERINFO_FETCH_FAILED("AUTH003", HttpStatus.BAD_REQUEST, "소셜 사용자 정보를 가져오지 못했습니다."),
        MEMBER_SAVE_FAILED("AUTH004", HttpStatus.INTERNAL_SERVER_ERROR, "회원 저장에 실패했습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}