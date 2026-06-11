package com.offnal.shifterz.global.util.encrypt;

import java.io.File;

/**
 * 양방향 암호화(대칭키/비대칭키) 기능을 정의하는 인터페이스입니다.
 * <p>
 * 암호화와 복호화가 모두 가능한 알고리즘을 사용하여 데이터의 기밀성을 보장합니다.
 * </p>
 */
public interface TwoWayEncryptor {

    /**
     * 평문을 암호화하여 바이트 배열로 반환합니다.
     *
     * @param plainText 암호화할 평문
     * @return 암호화된 데이터 (바이트 배열)
     */
    byte[] encrypt(String plainText);

    /**
     * 원본 파일을 암호화하여 대상 파일로 저장합니다.
     *
     * @param plainFile     암호화할 원본 파일
     * @param encryptedFile 암호화된 데이터가 저장될 대상 파일
     */
    void encryptFile(File plainFile, File encryptedFile);

    /**
     * 평문을 암호화하여 Base64 인코딩된 문자열로 반환합니다.
     *
     * @param plainText 암호화할 평문
     * @return Base64로 인코딩된 암호문
     */
    String encryptToBase64(String plainText);

    /**
     * 암호화된 바이트 배열을 복호화하여 평문으로 반환합니다.
     *
     * @param encryptedArray 복호화할 암호화 데이터 (바이트 배열)
     * @return 복호화된 평문
     */
    String decrypt(byte[] encryptedArray);

    /**
     * 암호화된 파일을 복호화하여 대상 파일로 저장합니다.
     *
     * @param encryptedFile 복호화할 암호화된 파일
     * @param plainFile     복호화된 데이터가 저장될 대상 파일
     */
    void decryptFile(File encryptedFile, File plainFile);

    /**
     * Base64로 인코딩된 암호문을 복호화하여 평문으로 반환합니다.
     *
     * @param base64EncodedText Base64로 인코딩된 암호문
     * @return 복호화된 평문
     */
    String decryptFromBase64(String base64EncodedText);
}
