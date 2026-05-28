package com.offnal.shifterz.domain.oauth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.oauth.apple.AppleLoginRequest;
import com.offnal.shifterz.domain.oauth.kakao.KakaoLoginRequest;
import lombok.Getter;

@Getter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "provider",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = KakaoLoginRequest.class, name = "KAKAO"),
        @JsonSubTypes.Type(value = AppleLoginRequest.class, name = "APPLE")
})
public abstract class LoginRequest {
    private Provider provider;
    private String token; // kakao: accessToken, apple: identityToken
}
