package com.offnal.shifterz.core.jwt.exception;

import com.offnal.shifterz.global.exception.CommonErrorCode;

import lombok.Getter;

@Getter
public class JwtAuthException extends RuntimeException {

	private final CommonErrorCode errorCode;

	public JwtAuthException(CommonErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}