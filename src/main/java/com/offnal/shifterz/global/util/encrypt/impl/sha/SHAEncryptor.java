package com.offnal.shifterz.global.util.encrypt.impl.sha;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.code.SHAErrorCode;
import com.offnal.shifterz.global.util.encrypt.OneWayEncryptor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * SHA 알고리즘을 사용한 단방향 해시 암호화 구현체입니다.
 * <p>
 * 솔트(Salt)를 사용하여 레인보우 테이블 공격에 대응하며,
 * 생성된 해시 값 앞부분에 솔트가 포함됩니다.
 * </p>
 */
@Slf4j
public class SHAEncryptor implements OneWayEncryptor {

    private static final int DEFAULT_SALT_LENGTH = 16; // 기본 솔트 길이 (16바이트)

    @Getter
    private final SHAType shaType;

    private final int saltLength;

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * 지정된 알고리즘과 솔트 길이를 사용하는 SHAEncryptor 생성자.
     *
     * @param shaType    사용할 SHA 알고리즘 타입
     * @param saltLength 생성할 솔트의 길이 (바이트)
     * @throws IllegalArgumentException 솔트 길이가 음수일 경우
     */
    public SHAEncryptor(@NonNull SHAType shaType, int saltLength) {
        this.shaType = shaType;
        if (saltLength < 0) {
            throw new IllegalArgumentException("솔트 길이는 음수일 수 없습니다: " + saltLength);
        }
        this.saltLength = saltLength;
    }

    /**
     * 지정된 알고리즘과 기본 솔트 길이를 사용하는 SHAEncryptor 생성자.
     *
     * @param shaType 사용할 SHA 알고리즘 타입
     */
    public SHAEncryptor(@NonNull SHAType shaType) {
        this(shaType, DEFAULT_SALT_LENGTH);
    }

    /**
     * SHA-256 알고리즘과 지정된 솔트 길이를 사용하는 SHAEncryptor 생성자.
     *
     * @param saltLength 생성할 솔트의 길이 (바이트)
     */
    public SHAEncryptor(int saltLength) {
        this(SHAType.SHA256, saltLength);
    }

    /**
     * SHA-256 알고리즘과 기본 솔트 길이를 사용하는 SHAEncryptor 생성자.
     */
    public SHAEncryptor() {
        this(SHAType.SHA256);
    }

    /**
     * 평문을 암호화(해시)하여 바이트 배열로 반환합니다.
     * <p>
     * 랜덤한 솔트를 생성하여 해시를 수행하고, [솔트 + 해시] 형태의 배열을 반환합니다.
     * </p>
     *
     * @param plainText 암호화할 평문
     * @return 솔트가 포함된 암호화된 바이트 배열
     */
    @Override
    public byte[] encrypt(@NonNull String plainText) {
        try {
            byte[] salt = generateSalt();
            return encrypt(plainText, salt);
        } catch (Exception e) {
            log.error("SHA 암호화 중 오류 발생", e);
            throw new CustomException(SHAErrorCode.ENCRYPT_FAILED);
        }
    }

    /**
     * 내부적인 암호화 로직을 수행합니다.
     *
     * @param plainText 암호화할 평문
     * @param salt      사용할 솔트
     * @return 솔트 + 해시된 바이트 배열
     */
    private byte[] encrypt(@NonNull String plainText, byte[] salt) {
        if (salt == null) {
            salt = new byte[0];
        }
        try {
            MessageDigest digest = shaType.getMessageDigest();
            digest.update(salt); // 솔트 추가
            digest.update(plainText.getBytes(StandardCharsets.UTF_8)); // 평문 추가
            byte[] hash = digest.digest(); // 해시 생성

            // 결과 배열 생성 (솔트 길이 + 해시 길이)
            byte[] result = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, result, 0, salt.length);
            System.arraycopy(hash, 0, result, salt.length, hash.length);
            return result;
        } catch (Exception e) {
            if (e instanceof NoSuchAlgorithmException) {
                log.error("지원하지 않는 해시 알고리즘입니다: {}", shaType, e);
            } else {
                log.error("SHA 해시 생성 중 오류 발생", e);
            }
            throw new CustomException(SHAErrorCode.ENCRYPT_FAILED);
        }
    }

    /**
     * 평문을 암호화하여 16진수 문자열로 반환합니다.
     *
     * @param plainText 암호화할 평문
     * @return 솔트가 포함된 16진수 해시 문자열
     */
    @Override
    public String encryptToHex(@NonNull String plainText) {
        try {
            byte[] saltedHash = encrypt(plainText);
            return Hex.encodeHexString(saltedHash);
        } catch (Exception e) {
            log.error("Hex 인코딩된 SHA 암호화에 실패했습니다.", e);
            throw new CustomException(SHAErrorCode.ENCRYPT_FAILED);
        }
    }

    /**
     * 평문이 암호화된 바이트 배열과 일치하는지 검증합니다.
     * <p>
     * 암호화된 배열의 앞부분에서 솔트를 추출하여 평문을 다시 해시한 후 비교합니다.
     * </p>
     *
     * @param plainText  검증할 평문
     * @param hashedText 비교할 솔트가 포함된 암호화 데이터
     * @return 일치 여부
     */
    @Override
    public boolean matches(@NonNull String plainText, byte[] hashedText) {
        if (hashedText == null || hashedText.length < saltLength) {
            return false;
        }

        try {
            // 암호문에서 솔트 추출
            byte[] salt = Arrays.copyOfRange(hashedText, 0, saltLength);

            // 평문과 추출한 솔트로 새로운 해시 생성
            byte[] newHash = encrypt(plainText, salt);

            // 두 해시 비교
            return MessageDigest.isEqual(newHash, hashedText);
        } catch (Exception e) {
            log.error("SHA 해시 검증 중 오류 발생", e);
            throw new CustomException(SHAErrorCode.MATCHES_FAILED);
        }
    }

    /**
     * 평문이 암호화된 16진수 문자열과 일치하는지 검증합니다.
     *
     * @param plainText  검증할 평문
     * @param hashedText 비교할 솔트가 포함된 16진수 해시 문자열
     * @return 일치 여부
     */
    @Override
    public boolean matches(@NonNull String plainText, String hashedText) {
        if (hashedText == null) {
            return false;
        }

        try {
            byte[] hashedBytes = Hex.decodeHex(hashedText);
            return matches(plainText, hashedBytes);
        } catch (Exception e) {
            log.error("16진수 해시 디코딩 중 오류 발생", e);
            throw new CustomException(SHAErrorCode.MATCHES_FAILED);
        }
    }

    /**
     * 랜덤한 솔트(Salt)를 생성합니다.
     *
     * @return 생성된 솔트 바이트 배열
     */
    private byte[] generateSalt() {
        try {
            byte[] salt = new byte[saltLength];
            secureRandom.nextBytes(salt);
            return salt;
        } catch (Exception e) {
            log.error("Salt 생성 중 오류 발생", e);
            throw new CustomException(SHAErrorCode.GENERATE_SALT_FAILED);
        }
    }
}
