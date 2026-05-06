package com.offnal.shifterz.domain.oauth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserInfoDto {
    private String providerId; // kakao: id, apple: sub
    private String email;
    private String nickname;
    private String profileImageUrl; // apple: null
    private String appleRefreshToken; // apple만 필요. kakao: null
}
