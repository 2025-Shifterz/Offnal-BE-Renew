package com.offnal.shifterz.global.util.encrypt;

/**
 * 단방향 암호화(해시) 기능을 정의하는 인터페이스입니다.
 * <p>
 * 주로 데이터 무결성 검증 등 복호화가 필요 없는 데이터 보호에 사용됩니다.
 * </p>
 */
public interface OneWayEncryptor {

    /**
     * 평문을 암호화(해시)하여 바이트 배열로 반환합니다.
     *
     * @param plainText 암호화할 평문
     * @return 암호화된 바이트 배열
     */
    byte[] encrypt(String plainText);

    /**
     * 평문을 암호화(해시)하여 16진수 문자열로 반환합니다.
     *
     * @param plainText 암호화할 평문
     * @return 암호화된 해시값 (16진수 문자열)
     */
    String encryptToHex(String plainText);

    /**
     * 평문이 주어진 암호화된 바이트 배열과 일치하는지 검증합니다.
     *
     * @param plainText  검증할 평문
     * @param hashedText 비교할 암호화된 바이트 배열
     * @return 일치 여부 (true/false)
     */
    boolean matches(String plainText, byte[] hashedText);

    /**
     * 평문이 주어진 암호화된 해시 문자열과 일치하는지 검증합니다.
     *
     * @param plainText  검증할 평문
     * @param hashedText 비교할 암호화된 해시 문자열
     * @return 일치 여부 (true/false)
     */
    boolean matches(String plainText, String hashedText);
}
