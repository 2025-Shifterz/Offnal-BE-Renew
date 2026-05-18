package com.offnal.shifterz.domain.oauth.apple.exception;

import com.offnal.shifterz.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AppleErrorCode implements ErrorCode {

    APPLE_TOKEN_INVALID("APL001", HttpStatus.UNAUTHORIZED, "유효하지 않은 Apple identity token입니다."),
    APPLE_PUBLIC_KEY_NOT_FOUND("APL002", HttpStatus.INTERNAL_SERVER_ERROR, "kid에 해당하는 Apple 공개키를 찾을 수 없습니다."),
    APPLE_PUBLIC_KEY_ERROR("APL003", HttpStatus.INTERNAL_SERVER_ERROR, "Apple 공개키 처리 중 오류가 발생했습니다."),
    APPLE_TOKEN_EXCHANGE_FAIL("APL004", HttpStatus.BAD_GATEWAY, "authorization_code 토큰 교환 실패"),
    APPLE_CLIENT_SECRET_ERROR("APL005", HttpStatus.INTERNAL_SERVER_ERROR, "client secret 생성 실패"),
    APPLE_REVOKE_FAIL("APL006", HttpStatus.BAD_GATEWAY, "Apple 토큰 revoke 실패");

    private final String code;
    private final HttpStatus status;
    private final String message;
}