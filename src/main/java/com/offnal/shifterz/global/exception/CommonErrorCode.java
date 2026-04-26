package com.offnal.shifterz.global.exception;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCode {

	INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
	INVALID_REQUEST("잘못된 요청입니다."),
	UNAUTHORIZED("인증이 필요합니다."),
	FORBIDDEN("접근이 거부되었습니다.");

	private final String message;

	CommonErrorCode(String message) {
		this.message = message;
	}
}