package com.offnal.shifterz.global.util.encrypt.impl.sha;

import java.security.MessageDigest;

/**
 * 지원하는 SHA 해시 알고리즘 타입을 정의하는 열거형입니다.
 */
public enum SHAType {
    /**
     * SHA-256 알고리즘. 보안성이 우수하며 널리 사용됩니다.
     */
    SHA256("SHA-256"),
    /**
     * SHA-512 알고리즘. 더 긴 해시 값을 생성하여 보안성을 높입니다.
     */
    SHA512("SHA-512"),
    /**
     * MD5 알고리즘. (참고: 보안 취약점으로 인해 중요 데이터 암호화에는 권장되지 않음)
     */
    MD5("MD5");

    /**
     * Java Security API에서 사용하는 알고리즘 이름
     */
    public final String algorithm;

    SHAType(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 해당 알고리즘을 사용하는 MessageDigest 인스턴스를 반환합니다.
     *
     * @return MessageDigest 인스턴스
     * @throws Exception 알고리즘을 찾을 수 없는 경우 발생
     */
    public MessageDigest getMessageDigest() throws Exception {
        return MessageDigest.getInstance(algorithm);
    }
}
