package com.offnal.shifterz.domain.oauth.apple;

import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.oauth.OAuthHandler;
import com.offnal.shifterz.domain.oauth.OAuthUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppleOAuthHandler implements OAuthHandler {
    private final AppleService appleService;

    @Override
    public Provider getProviderType() {
        return Provider.APPLE;
    }

    @Override
    public OAuthUserInfoDto toOAuthUserInfoDto(Object rawUserInfoDto) {
        AppleUserInfoResponseDto dto = (AppleUserInfoResponseDto) rawUserInfoDto;
        return OAuthUserInfoDto.builder()
                .providerId(dto.getSub())
                .email(dto.getEmail())
                .build();
    }

    public OAuthUserInfoDto getUserInfo(AppleLoginRequestDto request, String appleRefreshToken) {
        AppleUserInfoResponseDto rawInfo = appleService.getUserInfoFromIdentityToken(request);
        AppleAuthTokenResponseDto token = appleService.exchangeAuthorizationCode(request.getAuthorizationCode());

        return OAuthUserInfoDto.builder()
                .providerId(rawInfo.getSub())
                .email(request.getEmail() != null ? request.getEmail() : rawInfo.getEmail())
                .nickname(resolveNickname(request))
                .appleRefreshToken(token.getRefreshToken())
                .build();
    }

    private String resolveNickname(AppleLoginRequestDto request) {
        if (request.getFullName() != null) {
            String name = request.getFullName().getFullName();
            if (name != null && !name.isBlank()) return name;
        }
        return "Apple User";
    }
}
