package com.offnal.shifterz.global.exception;

import static com.offnal.shifterz.global.exception.ErrorCode.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offnal.shifterz.core.jwt.exception.JwtAuthException;
import com.offnal.shifterz.core.jwt.exception.TokenErrorCode;

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
			return ErrorStatusResolver.resolve(jwtAuthException.getErrorCode());
		}
		return HttpStatus.UNAUTHORIZED;
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
