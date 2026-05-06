package com.offnal.shifterz.global.util.encrypt;

import com.offnal.shifterz.global.util.encrypt.impl.aes.AESEncryptor;
import com.offnal.shifterz.global.util.encrypt.impl.sha.SHAEncryptor;
import com.offnal.shifterz.global.util.encrypt.impl.sha.SHAType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Base64;

/**
 * 암호화 및 해시 유틸리티 클래스입니다.
 * <p>
 * AES 암호화와 SHA 해시 기능을 제공하는 통합 유틸리티로,
 * 어플리케이션 내의 암호화 작업을 간편하게 수행할 수 있도록 돕습니다.
 * </p>
 */
@Component
public class EncryptUtil {
    private final AESEncryptor aesEncryptor;
    private final SHAEncryptor sha256Encryptor;
    private final SHAEncryptor sha512Encryptor;
    private static final int AES_GCM_NONCE_SIZE = 12;
    private static final int AES_GCM_TAG_SIZE_BYTES = 16;

    /**
     * EncryptUtil 생성자.
     * <p>
     * application.yml 파일에서 설정된 AES 키를 사용하여 AES 암호화 인스턴스를 초기화하고,
     * SHA-256 및 SHA-512 해시 인스턴스를 생성합니다.
     * </p>
     *
     * @param aesKey AES 암호화에 사용할 비밀 키
     * @throws IllegalArgumentException AES 키가 null이거나 비어있을 경우 발생
     */
    private EncryptUtil(@Value("${encrypt.aes.key}") String aesKey) {
        if (aesKey == null || aesKey.isEmpty()) {
            throw new IllegalArgumentException("AES 키는 null이거나 빈 문자열일 수 없습니다.");
        }
        this.aesEncryptor = new AESEncryptor(aesKey);
        this.sha256Encryptor = new SHAEncryptor(SHAType.SHA256);
        this.sha512Encryptor = new SHAEncryptor(SHAType.SHA512);
    }

    /**
     * 주어진 평문을 AES 알고리즘으로 암호화하여 Base64 문자열로 반환합니다.
     *
     * @param plainText 암호화할 평문
     * @return Base64로 인코딩된 암호화 문자열
     */
    public String encryptAES(@NonNull String plainText) {
        return aesEncryptor.encryptToBase64(plainText);
    }

    /**
     * Base64로 인코딩된 AES 암호문을 복호화하여 평문으로 반환합니다.
     *
     * @param base64EncodedText Base64로 인코딩된 암호화 문자열
     * @return 복호화된 평문
     */
    public String decryptAES(@NonNull String base64EncodedText) {
        return aesEncryptor.decryptFromBase64(base64EncodedText);
    }

    /**
     * 파일을 AES 알고리즘으로 암호화합니다.
     *
     * @param plainFile     암호화할 원본 파일
     * @param encryptedFile 암호화된 데이터가 저장될 대상 파일
     */
    public void encryptFile(@NonNull File plainFile, @NonNull File encryptedFile) {
        aesEncryptor.encryptFile(plainFile, encryptedFile);
    }

    /**
     * 암호화된 파일을 AES 알고리즘으로 복호화합니다.
     *
     * @param encryptedFile 암호화된 파일
     * @param plainFile     복호화된 데이터가 저장될 대상 파일
     */
    public void decryptFile(@NonNull File encryptedFile, @NonNull File plainFile) {
        aesEncryptor.decryptFile(encryptedFile, plainFile);
    }

    /**
     * 주어진 평문을 SHA-256 알고리즘으로 해시하여 16진수 문자열로 반환합니다.
     *
     * @param plainText 해시할 평문
     * @return SHA-256 해시값 (16진수 문자열)
     */
    public String hashSHA256(@NonNull String plainText) {
        return sha256Encryptor.encryptToHex(plainText);
    }

    /**
     * 평문이 주어진 SHA-256 해시값과 일치하는지 검증합니다.
     *
     * @param plainText  검증할 평문
     * @param hashedText 비교할 SHA-256 해시값
     * @return 일치하면 true, 그렇지 않으면 false
     */
    public boolean verifySHA256(@NonNull String plainText, @NonNull String hashedText) {
        return sha256Encryptor.matches(plainText, hashedText);
    }

    /**
     * 주어진 평문을 SHA-512 알고리즘으로 해시하여 16진수 문자열로 반환합니다.
     *
     * @param plainText 해시할 평문
     * @return SHA-512 해시값 (16진수 문자열)
     */
    public String hashSHA512(@NonNull String plainText) {
        return sha512Encryptor.encryptToHex(plainText);
    }

    /**
     * 평문이 주어진 SHA-512 해시값과 일치하는지 검증합니다.
     *
     * @param plainText  검증할 평문
     * @param hashedText 비교할 SHA-512 해시값
     * @return 일치하면 true, 그렇지 않으면 false
     */
    public boolean verifySHA512(@NonNull String plainText, @NonNull String hashedText) {
        return sha512Encryptor.matches(plainText, hashedText);
    }

    public String encryptAESOrNull(String value) {
        if (value == null) return null;
        if (value.isBlank()) return null;


        // 이미 암호문이면 그대로 반환 (중복 암호화 방지)
        if (isProbablyAesGcmBase64(value)) return value;

        return encryptAES(value);
    }

    public String decryptAESOrNull(String value) {
        if (value == null) return null;
        if (value.isBlank()) return null;


        return decryptAES(value);
    }

    private boolean isProbablyAesGcmBase64(String value) {
        try {
            byte[] decoded = Base64.getDecoder().decode(value);
            return decoded.length >= (AES_GCM_NONCE_SIZE + AES_GCM_TAG_SIZE_BYTES);
        } catch (IllegalArgumentException e) {
            return false; // Base64 자체가 아니면 평문
        }
    }
}
