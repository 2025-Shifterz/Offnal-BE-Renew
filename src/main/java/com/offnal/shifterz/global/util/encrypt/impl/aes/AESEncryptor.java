package com.offnal.shifterz.global.util.encrypt.impl.aes;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.global.util.encrypt.TwoWayEncryptor;
import com.offnal.shifterz.global.util.encrypt.impl.sha.SHAEncryptor;
import com.offnal.shifterz.global.util.encrypt.impl.sha.SHAType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256 알고리즘을 사용한 양방향 암호화 구현체입니다.
 * <p>
 * GCM (Galois/Counter Mode) 모드를 사용하여 데이터의 기밀성뿐만 아니라 무결성도 보장합니다.
 * </p>
 */
@Slf4j
public class AESEncryptor implements TwoWayEncryptor {

    private static final int NONCE_SIZE = 12; // GCM 모드의 표준 Nonce 크기 (12 바이트)
    private static final int TAG_SIZE = 128; // GCM 인증 태그 크기 (128 비트)

    private static final int FILE_BUFFER_SIZE = 8192; // 파일 입출력 버퍼 크기

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private final SecretKeySpec secretKey;

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * AESEncryptor 생성자.
     * <p>
     * 입력받은 키를 SHA-256으로 해시하여 32바이트(256비트) 길이의 AES 키를 생성합니다.
     * 이를 통해 입력 키의 길이와 상관없이 항상 256비트 키를 사용합니다.
     * </p>
     *
     * @param key 암호화에 사용할 비밀 키 문자열
     */
    public AESEncryptor(@NonNull String key) {
        try {
            secretKey = generateSecretKeySpec(new SHAEncryptor(SHAType.SHA256, 0).encrypt(key));
        } catch (Exception e) {
            log.error("AESEncryptor 객체 생성에 실패했습니다.", e);
            throw new CustomException(AESErrorCode.AES_ENCRYPTOR_CONSTRUCTION_FAILED);
        }
    }

    /**
     * Cipher 인스턴스를 생성하여 반환합니다.
     *
     * @return AES/GCM/NoPadding 변환을 사용하는 Cipher 객체
     */
    private Cipher getCipher() {
        try {
            return Cipher.getInstance(TRANSFORMATION);
        } catch (Exception e) {
            if (e instanceof NoSuchAlgorithmException) {
                log.error("Cipher 알고리즘이 지원되지 않습니다: {}", TRANSFORMATION, e);
            } else if (e instanceof NoSuchPaddingException) {
                log.error("Cipher 패딩이 지원되지 않습니다: {}", TRANSFORMATION, e);
            } else {
                log.error("Cipher 객체 생성 중 예기치 않은 오류가 발생했습니다.", e);
            }
            throw new CustomException(AESErrorCode.GET_CIPHER_FAILED);
        }
    }

    /**
     * 바이트 배열 키로부터 SecretKeySpec 객체를 생성합니다.
     *
     * @param key 32바이트 길이의 키 데이터
     * @return 생성된 SecretKeySpec 객체
     */
    private SecretKeySpec generateSecretKeySpec(byte[] key) {
        if (key == null || key.length != 32) {
            throw new IllegalArgumentException("키는 32바이트 길이여야 합니다. 현재 길이: " + (key == null ? "null" : key.length));
        }
        try {
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            log.error("SecretKeySpec 생성에 실패했습니다.", e);
            throw new CustomException(AESErrorCode.GET_SECRET_KEY_SPEC_FAILED);
        }
    }

    /**
     * Cipher 객체를 초기화합니다.
     *
     * @param mode   Cipher.ENCRYPT_MODE 또는 Cipher.DECRYPT_MODE
     * @param nonce  초기화 벡터 (Nonce)
     * @param cipher 초기화할 Cipher 객체
     */
    private void initCipher(int mode, byte[] nonce, @NonNull Cipher cipher) {
        if (nonce == null || nonce.length != NONCE_SIZE) {
            throw new IllegalArgumentException("Nonce는 " + NONCE_SIZE + "바이트 길이여야 합니다. 현재 길이: " + (nonce == null ? "null" : nonce.length));
        }
        try {
            cipher.init(mode, secretKey, new GCMParameterSpec(TAG_SIZE, nonce));
        } catch (Exception e) {
            if (e instanceof UnsupportedOperationException) {
                log.error("Cipher 초기화에 실패했습니다. 지원되지 않는 모드: {}", mode, e);
            } else if (e instanceof InvalidKeyException) {
                log.error("Cipher 초기화에 실패했습니다. 유효하지 않은 키입니다.", e);
            } else if (e instanceof InvalidAlgorithmParameterException) {
                log.error("Cipher 초기화에 실패했습니다. 유효하지 않은 알고리즘 매개변수입니다.", e);
            } else {
                log.error("Cipher 초기화 중 예기치 않은 오류가 발생했습니다.", e);
            }
            throw new CustomException(AESErrorCode.CIPHER_INIT_FAILED);
        }
    }

    /**
     * 랜덤한 초기화 벡터(Nonce)를 생성합니다.
     *
     * @return 12바이트 길이의 랜덤 Nonce
     */
    private byte[] generateNonce() {
        try {
            byte[] nonce = new byte[NONCE_SIZE];
            secureRandom.nextBytes(nonce);
            return nonce;
        } catch (Exception e) {
            throw new CustomException(AESErrorCode.GENERATE_NONCE_FAILED);
        }
    }

    /**
     * 평문을 AES-GCM으로 암호화합니다.
     * <p>
     * 결과 데이터 구조: [Nonce (12바이트)] + [암호문 + 인증 태그]
     * </p>
     *
     * @param plainText 암호화할 평문
     * @return 암호화된 데이터 바이트 배열
     */
    @Override
    public byte[] encrypt(@NonNull String plainText) {
        try {
            Cipher eCipher = getCipher();
            byte[] nonce = generateNonce();
            initCipher(Cipher.ENCRYPT_MODE, nonce, eCipher);

            byte[] encrypted = eCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedWithNonce = new byte[NONCE_SIZE + encrypted.length];

            // Nonce를 암호문 앞에 붙임
            System.arraycopy(nonce, 0, encryptedWithNonce, 0, NONCE_SIZE);
            System.arraycopy(encrypted, 0, encryptedWithNonce, NONCE_SIZE, encrypted.length);
            return encryptedWithNonce;
        } catch (Exception e) {
            if (e instanceof IllegalBlockSizeException) {
                log.error("AES 암호화 중 블록 크기 오류가 발생했습니다.", e);
            } else if (e instanceof BadPaddingException) {
                log.error("AES 암호화 중 패딩 오류가 발생했습니다.", e);
            } else {
                log.error("AES 암호화 중 예기치 않은 오류가 발생했습니다.", e);
            }
            throw new CustomException(AESErrorCode.ENCRYPT_FAILED);
        }
    }

    /**
     * 파일을 암호화하여 저장합니다.
     * <p>
     * 대용량 파일 처리를 위해 스트림 방식을 사용합니다.
     * 암호화된 파일의 맨 앞에는 Nonce가 저장됩니다.
     * </p>
     *
     * @param plainFile     암호화할 원본 파일
     * @param encryptedFile 암호화된 파일이 저장될 경로
     */
    @Override
    public void encryptFile(@NonNull File plainFile, @NonNull File encryptedFile) {
        if (!plainFile.exists() || !plainFile.isFile()) {
            throw new IllegalArgumentException("암호화할 파일이 존재하지 않거나 올바른 파일이 아닙니다: " + plainFile.getAbsolutePath());
        }

        File parentDir = encryptedFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new RuntimeException("암호화된 파일의 부모 디렉토리를 생성하는 데 실패했습니다: " + parentDir.getAbsolutePath());
            }
        }

        try {
            Cipher eCipher = getCipher();
            byte[] nonce = generateNonce();

            initCipher(Cipher.ENCRYPT_MODE, nonce, eCipher);


            try (FileInputStream fis = new FileInputStream(plainFile);
                 FileOutputStream fos = new FileOutputStream(encryptedFile);
                 CipherOutputStream cos = new CipherOutputStream(fos, eCipher)) {

                // Nonce를 파일 처음에 기록
                fos.write(nonce);

                byte[] buffer = new byte[FILE_BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            if (e instanceof IOException) {
                log.error("파일 암호화 중 입출력 오류가 발생했습니다: {}", plainFile.getAbsolutePath(), e);
            } else {
                log.error("파일 암호화 중 예기치 않은 오류가 발생했습니다: {}", plainFile.getAbsolutePath(), e);
            }
            throw new CustomException(AESErrorCode.ENCRYPT_FAILED);
        }
    }

    /**
     * 평문을 암호화하여 Base64 문자열로 반환합니다.
     *
     * @param plainText 암호화할 평문
     * @return Base64 인코딩된 암호문
     */
    @Override
    public String encryptToBase64(@NonNull String plainText) {
        try {
            byte[] encryptedBytes = encrypt(plainText);
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Base64 인코딩된 AES 암호화에 실패했습니다.", e);
            throw new CustomException(AESErrorCode.ENCRYPT_FAILED);
        }
    }

    /**
     * 암호화된 바이트 배열을 복호화합니다.
     * <p>
     * 데이터의 앞부분에서 Nonce를 추출하고, 이를 사용하여 나머지 데이터를 복호화합니다.
     * </p>
     *
     * @param encryptedArray 복호화할 데이터 (Nonce + 암호문)
     * @return 복호화된 평문
     * @throws IllegalArgumentException 데이터 길이가 너무 짧을 경우
     */
    @Override
    public String decrypt(byte[] encryptedArray) {
        if (encryptedArray == null || encryptedArray.length < NONCE_SIZE + TAG_SIZE / 8) {
            throw new IllegalArgumentException("암호화된 데이터가 올바르지 않습니다.");
        }

        try {
            Cipher dCipher = getCipher();
            byte[] nonce = new byte[NONCE_SIZE];

            // Nonce 추출
            System.arraycopy(encryptedArray, 0, nonce, 0, NONCE_SIZE);
            initCipher(Cipher.DECRYPT_MODE, nonce, dCipher);

            byte[] encrypted = new byte[encryptedArray.length - NONCE_SIZE];
            System.arraycopy(encryptedArray, NONCE_SIZE, encrypted, 0, encrypted.length);

            byte[] decrypted = dCipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            if (e instanceof IllegalBlockSizeException) {
                log.error("AES 복호화 중 블록 크기 오류가 발생했습니다.", e);
            } else if (e instanceof BadPaddingException) {
                log.error("AES 복호화 중 패딩 오류가 발생했습니다. 데이터가 손상되었거나 키가 올바르지 않을 수 있습니다.", e);
            } else {
                log.error("AES 복호화 중 예기치 않은 오류가 발생했습니다.", e);
            }
            throw new CustomException(AESErrorCode.DECRYPT_FAILED);
        }
    }

    /**
     * 암호화된 파일을 복호화하여 저장합니다.
     * <p>
     * 파일의 첫 12바이트를 읽어 Nonce로 사용하고, 나머지 데이터를 복호화합니다.
     * </p>
     *
     * @param encryptedFile 암호화된 파일
     * @param plainFile     복호화된 파일이 저장될 경로
     */
    @Override
    public void decryptFile(@NonNull File encryptedFile, @NonNull File plainFile) {
        if (!encryptedFile.exists() || !encryptedFile.isFile()) {
            throw new IllegalArgumentException("복호화할 파일이 존재하지 않거나 올바른 파일이 아닙니다: " + encryptedFile.getAbsolutePath());
        }

        File tempFile = null;
        try {
            Cipher dCipher = getCipher();

            File parentDir = plainFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new RuntimeException("복호화된 파일의 부모 디렉토리를 생성하는 데 실패했습니다: " + parentDir.getAbsolutePath());
                }
            }

            tempFile = new File(parentDir, plainFile.getName() + ".tmp." + java.util.UUID.randomUUID());

            try (FileInputStream fis = new FileInputStream(encryptedFile);
                 FileOutputStream fos = new FileOutputStream(tempFile)) {

                byte[] nonce = fis.readNBytes(NONCE_SIZE);
                if (nonce.length != NONCE_SIZE) {
                    throw new IllegalArgumentException("암호화된 파일 형식이 올바르지 않습니다. Nonce를 읽을 수 없습니다.");
                }

                initCipher(Cipher.DECRYPT_MODE, nonce, dCipher);

                try (CipherInputStream cis = new CipherInputStream(fis, dCipher)) {
                    byte[] buffer = new byte[FILE_BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = cis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
            }

            // 복호화 성공 시 임시 파일을 원본 파일로 이동 (덮어쓰기)
            Files.move(tempFile.toPath(), plainFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            // 실패 시 임시 파일 삭제
            if (tempFile != null && tempFile.exists()) {
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException ex) {
                    log.warn("복호화 실패 후 임시 파일 삭제 중 오류 발생: {}", tempFile.getAbsolutePath(), ex);
                }
            }

            if (e instanceof IOException) {
                log.error("파일 복호화 중 입출력 오류가 발생했습니다: {}", encryptedFile.getAbsolutePath(), e);
            } else {
                log.error("파일 복호화 중 예기치 않은 오류가 발생했습니다: {}", encryptedFile.getAbsolutePath(), e);
            }
            throw new CustomException(AESErrorCode.DECRYPT_FAILED);
        }
    }

    /**
     * Base64 인코딩된 암호문을 복호화합니다.
     *
     * @param base64EncodedText Base64 인코딩된 암호문
     * @return 복호화된 평문
     */
    @Override
    public String decryptFromBase64(@NonNull String base64EncodedText) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(base64EncodedText);
            return decrypt(encryptedBytes);
        } catch (Exception e) {
            log.error("Base64로 인코딩된 AES 복호화에 실패했습니다.", e);
            throw new CustomException(AESErrorCode.DECRYPT_FAILED);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum AESErrorCode implements ErrorReason {
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
}
