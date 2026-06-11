package com.offnal.shifterz.domain.oauth;

import com.offnal.shifterz.domain.member.domain.Provider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserInfo {
    private Provider provider;
    private String providerId; // kakao: id, apple: sub
    private String email;
    private String nickname;
    private String profileImageUrl; // apple: null
    private String appleRefreshToken; // apple만 필요. kakao: null
}
