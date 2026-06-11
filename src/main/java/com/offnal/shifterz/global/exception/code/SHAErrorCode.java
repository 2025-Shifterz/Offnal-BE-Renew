package com.offnal.shifterz.global.exception.code;

import com.offnal.shifterz.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SHAErrorCode implements ErrorCode {
    SHA_ENCRYPTOR_CONSTRUCTION_FAILED("SHA001", HttpStatus.INTERNAL_SERVER_ERROR, "SHAEncryptor 인스턴스 생성에 실패하였습니다."),
    ENCRYPT_FAILED("SHA002", HttpStatus.INTERNAL_SERVER_ERROR, "SHA 암호화(해시 생성)에 실패하였습니다."),
    MATCHES_FAILED("SHA003", HttpStatus.INTERNAL_SERVER_ERROR, "SHA 해시 검증에 실패하였습니다."),
    GENERATE_SALT_FAILED("SHA004", HttpStatus.INTERNAL_SERVER_ERROR, "Salt 생성에 실패하였습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

}
