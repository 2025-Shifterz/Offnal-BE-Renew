package com.offnal.shifterz.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offnal.shifterz.jwt.exception.JwtAuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
            return createJwtErrorResponse(jwtAuthException);
        }

        return createDefaultErrorResponse(authException);
    }

    private ErrorResponse createJwtErrorResponse(JwtAuthException jwtAuthException) {
        ErrorCode errorCode = jwtAuthException.getErrorCode();
        return ErrorResponse.from(errorCode);
    }

    private ErrorResponse createDefaultErrorResponse(AuthenticationException authException) {
        String message = authException.getMessage() != null
                ? authException.getMessage()
                : "인증되지 않은 요청입니다.";

        return ErrorResponse.of("AUTHENTICATION_FAILED", message);
    }

    private HttpStatus determineHttpStatus(AuthenticationException authException) {
        if (authException.getCause() instanceof JwtAuthException jwtAuthException) {
            return jwtAuthException.getErrorCode().getHttpStatus();
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
