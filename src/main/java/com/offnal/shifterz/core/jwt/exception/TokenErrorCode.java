package com.offnal.shifterz.core.jwt.exception;

import com.offnal.shifterz.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public enum TokenErrorCode implements ErrorCode {

	INVALID_TOKEN("유효하지 않은 토큰입니다."),
	INVALID_REFRESH_TOKEN("유효하지 않은 Refresh Token입니다."),
	LOGOUT_TOKEN("이미 로그아웃된 토큰입니다.");

	private final String message;

	TokenErrorCode(String message) {
		this.message = message;
	}
}
