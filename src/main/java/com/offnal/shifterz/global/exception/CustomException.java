package com.offnal.shifterz.global.exception;


import com.offnal.shifterz.core.jwt.exception.TokenErrorCode;

import lombok.Getter;
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
