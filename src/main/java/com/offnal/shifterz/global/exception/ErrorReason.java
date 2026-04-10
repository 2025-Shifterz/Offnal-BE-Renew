package com.offnal.shifterz.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorReason {
    String getCode();
    HttpStatus getStatus();
    String getMessage();
}
