package com.offnal.shifterz.domain.oauth;

import com.offnal.shifterz.domain.member.domain.Provider;

public interface OAuthHandler {
    Provider getProviderType();

    OAuthUserInfoDto toOAuthUserInfoDto(Object rawUserInfoDto);
}
