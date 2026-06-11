package com.offnal.shifterz.global.util;

import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import com.offnal.shifterz.global.util.dto.PresignedUrlResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.UUID;

import static software.amazon.awssdk.core.sync.RequestBody.fromBytes;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final MemberRepository memberRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private static final String FOLDER = "profile";
    private static final long PRESIGNED_URL_DURATION_MINUTES = 10;



    // S3에 이미지 업로드할 presigned url 발급
    public PresignedUrlResponse createPresignedUrl(String extension, String existingKey){
        extension = normalizeExtension(extension);
        String contentType = getContentTypeFromExtension(extension);

        String key = StringUtils.hasText(existingKey)
                ? existingKey
                : FOLDER + "/" + UUID.randomUUID() + "." + extension;


        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                r -> r.putObjectRequest(objectRequest)
                        .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_DURATION_MINUTES))
        );

        return new PresignedUrlResponse(presignedRequest.url().toString(), key);
    }

    private String normalizeExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            throw new CustomException(S3ErrorCode.UNSUPPORTED_CONTENT_TYPE);
        }
        return extension.replace(".","").toLowerCase();
    }

    // 확장자 get
    private String getContentTypeFromExtension(String extension) {
        return switch (extension.toLowerCase()){
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> throw new CustomException(S3ErrorCode.UNSUPPORTED_CONTENT_TYPE);
        };
    }

    // S3의 이미지 조회용 presigned url 발급
    public String generateViewPresignedUrl(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(
                r -> r.signatureDuration(Duration.ofMinutes(PRESIGNED_URL_DURATION_MINUTES))
                        .getObjectRequest(getObjectRequest)
        );

        return presignedGetObjectRequest.url().toString();
    }

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } catch (Exception e) {
            throw new CustomException(S3ErrorCode.S3_DELETE_FAILED);
        }
    }

    // 프로필 이미지 S3에 직접 업로드
    public String uploadImageBytes(byte[] bytes, String key) {
        try {
            String contentType = getContentTypeFromKey(key);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, fromBytes(bytes));

            return key;
        } catch (Exception e) {
            throw new CustomException(S3ErrorCode.UPLOAD_TO_S3_FAILED);
        }
    }

    private String getContentTypeFromKey(String key) {
        if (key.endsWith(".png")) return "image/png";
        if (key.endsWith(".jpg")) return "image/jpeg";
        if (key.endsWith(".jpeg")) return "image/jpeg";

        throw new CustomException(S3ErrorCode.UNSUPPORTED_CONTENT_TYPE);
    }

    // imageUrl 다운로드
    public byte[] downloadImageFromUrl(String imageUrl) {
        try (var in = new java.net.URL(imageUrl).openStream()) {
            return in.readAllBytes();
        } catch (Exception e) {
            throw new CustomException(S3ErrorCode.UPLOAD_TO_S3_FAILED);
        }
    }


    @Getter
    @AllArgsConstructor
    public enum S3ErrorCode implements ErrorCode {
        S3_UPLOAD_FAILED("S3001", HttpStatus.INTERNAL_SERVER_ERROR, "프로필 사진을 S3 업로드 실패하였습니다."),
        S3_DELETE_FAILED("S3002", HttpStatus.INTERNAL_SERVER_ERROR, "S3에 업로드된 프로필 사진을 삭제하는 데에 실패하였습니다."),
        S3_KEY_ALREADY_EXISTS("S3003", HttpStatus.BAD_REQUEST, "이미 프로필 이미지 Key가 존재하는 회원입니다."),
        S3_KEY_NOT_FOUND("S3004", HttpStatus.NOT_FOUND, "존재하지 않는 S3 Key입니다."),
        UPLOAD_TO_S3_FAILED("S3005", HttpStatus.INTERNAL_SERVER_ERROR, "S3에 사진 업로드를 실패하였습니다."),
        UNSUPPORTED_CONTENT_TYPE("S3006", HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 파일 확장자입니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}
