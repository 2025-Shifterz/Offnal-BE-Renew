package com.offnal.shifterz.global.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offnal.shifterz.core.jwt.exception.JwtAuthException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException) throws IOException {

		ErrorResponse errorResponse = createErrorResponse(authException);
		HttpStatus status = determineHttpStatus(authException);

		writeErrorResponse(response, errorResponse, status);
	}

	private ErrorResponse createErrorResponse(AuthenticationException authException) {
		if (authException.getCause() instanceof JwtAuthException jwtAuthException) {
			return ErrorResponse.from(jwtAuthException.getErrorCode());
		}

		return ErrorResponse.of("인증되지 않은 요청입니다.");
	}

	private HttpStatus determineHttpStatus(AuthenticationException authException) {
		if (authException.getCause() instanceof JwtAuthException jwtAuthException) {
			return resolveStatus(jwtAuthException.getErrorCode());
		}
		return HttpStatus.UNAUTHORIZED;
	}

	private HttpStatus resolveStatus(CommonErrorCode errorCode) {
		return switch (errorCode) {
			case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
			case FORBIDDEN -> HttpStatus.FORBIDDEN;
			case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
			default -> HttpStatus.BAD_REQUEST;
		};
	}

	private void writeErrorResponse(HttpServletResponse response,
		ErrorResponse errorResponse,
		HttpStatus status) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.setStatus(status.value());
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
