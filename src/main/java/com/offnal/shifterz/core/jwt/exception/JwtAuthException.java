package com.offnal.shifterz.jwt.exception;

import com.offnal.shifterz.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class JwtAuthException extends RuntimeException {
    private final ErrorCode errorCode;

    public JwtAuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
