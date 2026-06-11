package com.offnal.shifterz.global.exception.code;

import com.offnal.shifterz.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AESErrorCode implements ErrorCode {
    GET_CIPHER_FAILED("AES001", HttpStatus.INTERNAL_SERVER_ERROR, "AES Cipher 객체 생성에 실패하였습니다."),
    GET_SECRET_KEY_SPEC_FAILED("AES002", HttpStatus.INTERNAL_SERVER_ERROR, "SecretKeySpec 객체 생성에 실패하였습니다."),
    AES_ENCRYPTOR_CONSTRUCTION_FAILED("AES003", HttpStatus.INTERNAL_SERVER_ERROR, "AESEncryptor 객체 생성에 실패하였습니다."),
    CIPHER_INIT_FAILED("AES004", HttpStatus.INTERNAL_SERVER_ERROR, "Cipher 초기화에 실패하였습니다."),
    GENERATE_NONCE_FAILED("AES005", HttpStatus.INTERNAL_SERVER_ERROR, "Nonce 생성에 실패하였습니다."),
    ENCRYPT_FAILED("AES006", HttpStatus.INTERNAL_SERVER_ERROR, "AES 암호화에 실패하였습니다."),
    DECRYPT_FAILED("AES007", HttpStatus.INTERNAL_SERVER_ERROR, "AES 복호화에 실패하였습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
