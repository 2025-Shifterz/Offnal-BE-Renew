package com.offnal.shifterz.domain.member.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.offnal.shifterz.domain.oauth.apple.AppleLoginRequest;
import com.offnal.shifterz.domain.oauth.apple.AppleUserInfoResponseDto;

public interface AppleSocialService {

    AppleUserInfoResponseDto getUserInfoFromIdentityToken(AppleLoginRequest request);

    DecodedJWT verifyIdentityToken(String identityToken);
}
