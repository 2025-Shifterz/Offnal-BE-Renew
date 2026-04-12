package com.offnal.shifterz.global.exception;


import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;     // 공통 에러코드
    private final ErrorReason errorReason; // 도메인별 에러코드

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorReason = null;
    }

    public CustomException(ErrorReason errorReason) {
        super(errorReason.getMessage());
        this.errorReason = errorReason;
        this.errorCode = null;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public ErrorReason getErrorReason() {
        return errorReason;
    }
}
