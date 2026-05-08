package com.offnal.shifterz.domain.oauth.exception;

import com.offnal.shifterz.global.exception.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OAuthErrorCode implements ErrorReason {
    UNSUPPORTED_PROVIDER("AUTH001", HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 제공자입니다."),
    INVALID_SOCIAL_TOKEN("AUTH002", HttpStatus.UNAUTHORIZED, "유효하지 않은 소셜 액세스 토큰입니다."),
    SOCIAL_USERINFO_FETCH_FAILED("AUTH003", HttpStatus.BAD_REQUEST, "소셜 사용자 정보를 가져오지 못했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}