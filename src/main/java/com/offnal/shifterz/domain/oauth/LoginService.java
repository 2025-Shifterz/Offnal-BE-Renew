package com.offnal.shifterz.domain.oauth;

import com.offnal.shifterz.core.jwt.JwtTokenProvider;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.dto.AuthResponseDto;
import com.offnal.shifterz.domain.member.dto.MemberResponseDto;
import com.offnal.shifterz.domain.member.exception.MemberErrorCode;
import com.offnal.shifterz.domain.member.service.MemberService;
import com.offnal.shifterz.domain.oauth.apple.*;
import com.offnal.shifterz.domain.oauth.exception.OAuthErrorCode;
import com.offnal.shifterz.domain.oauth.kakao.KakaoLoginRequestDto;
import com.offnal.shifterz.domain.oauth.kakao.KakaoOAuthHandler;
import com.offnal.shifterz.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final OAuthHandlerFactory oAuthHandlerFactory;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponseDto login(LoginRequestDto dto) {
        OAuthHandler handler = oAuthHandlerFactory.getProvider(dto.getProvider());
        OAuthUserInfoDto userInfo = handler.getUserInfo(dto);
        return processLogin(userInfo);
    }

    private AuthResponseDto processLogin(OAuthUserInfoDto userInfo) {
        MemberResponseDto.MemberRegisterResponseDto result = memberService.registerMemberIfAbsent(
                userInfo.getProvider(),
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
        String accessToken = jwtTokenProvider.createAccessToken(result.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(result.getId());
        return AuthResponseDto.from(result, accessToken, refreshToken);
    }
}