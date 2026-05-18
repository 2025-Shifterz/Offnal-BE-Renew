package com.offnal.shifterz.domain.oauth;

import com.offnal.shifterz.core.jwt.JwtTokenProvider;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.dto.AuthResponseDto;
import com.offnal.shifterz.domain.member.dto.MemberResponseDto;
import com.offnal.shifterz.domain.member.exception.MemberErrorCode;
import com.offnal.shifterz.domain.member.service.MemberService;
import com.offnal.shifterz.domain.oauth.apple.*;
import com.offnal.shifterz.domain.oauth.exception.OAuthErrorCode;
import com.offnal.shifterz.domain.oauth.kakao.KakaoLoginRequest;
import com.offnal.shifterz.domain.oauth.kakao.KakaoOAuthHandler;
import com.offnal.shifterz.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final KakaoOAuthHandler kakaoOAuthHandler;
    private final AppleOAuthHandler appleOAuthHandler;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponseDto loginWithAppleNative(AppleLoginRequest request) {

        OAuthUserInfoDto userInfo = appleOAuthHandler.getUserInfo(request, null);
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
            throw new CustomException(MemberErrorCode.MEMBER_SAVE_FAILED);
        }
        String jwtAccessToken = jwtTokenProvider.createAccessToken(result.getId());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(result.getId());
        return AuthResponseDto.from(result, jwtAccessToken, jwtRefreshToken);
    }


    public AuthResponseDto loginWithKakaoNative(KakaoLoginRequest request) {
        try {
            OAuthUserInfoDto userInfo = kakaoOAuthHandler.getUserInfo(request.getAccessToken());
            return processLogin(Provider.KAKAO, userInfo);
        } catch (Exception e) {
            throw new CustomException(OAuthErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }

}