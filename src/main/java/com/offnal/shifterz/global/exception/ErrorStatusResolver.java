package com.offnal.shifterz.global.exception;

import com.offnal.shifterz.global.exception.code.CommonErrorCode;
import org.springframework.http.HttpStatus;

import com.offnal.shifterz.core.jwt.exception.TokenErrorCode;

public final class ErrorStatusResolver {

	private ErrorStatusResolver() {
	}

	public static HttpStatus resolve(ErrorCode errorCode) {
		if (errorCode instanceof TokenErrorCode) {
			return HttpStatus.UNAUTHORIZED;
		}

		if (errorCode instanceof CommonErrorCode commonErrorCode) {
			return switch (commonErrorCode) {
				case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
				case FORBIDDEN -> HttpStatus.FORBIDDEN;
				case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
				default -> HttpStatus.BAD_REQUEST;
			};
		}

		return HttpStatus.BAD_REQUEST;
	}
}