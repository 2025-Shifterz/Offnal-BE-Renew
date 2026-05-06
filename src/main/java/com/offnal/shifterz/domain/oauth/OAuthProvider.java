package com.offnal.shifterz.domain.oauth;

import com.offnal.shifterz.domain.member.domain.Provider;

public interface OAuthProvider {
    Provider getProviderType();

    OAuthUserInfoDto toOAuthUserInfoDto(Object rawUserInfoDto);
}
