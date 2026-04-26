package com.offnal.shifterz.global.exception;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

	private final String message;
	private final Map<String, String> errors;

	public static ErrorResponse of(String message) {
		return ErrorResponse.builder()
			.message(message)
			.build();
	}

	public static ErrorResponse of(String message, Map<String, String> errors) {
		return ErrorResponse.builder()
			.message(message)
			.errors(errors)
			.build();
	}

	public static ErrorResponse from(ErrorCode errorCode) {
		return ErrorResponse.of(errorCode.getMessage());
	}
}