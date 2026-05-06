package com.offnal.shifterz.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberResponseDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberRegisterResponseDto {
        private Long id;
        private String email;
        private String memberName;
        private String phoneNumber;
        private String profileImageKey;
        private boolean isNewMember;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "회원 프로필 수정 응답 DTO")
    public static class MemberUpdateResponseDto {
        private Long id;

        @Schema(example = "user@example.com", description = "이메일 주소")
        private String email;

        @Schema(example = "홍길동", description = "회원 이름")
        private String memberName;

        @Schema(example = "010-1111-1111", description = "전화번호")
        private String phoneNumber;

        @Schema(example = "profile/abcde12345-test.jpg",
                description = "S3에 저장된 프로필 이미지 key")
        private String profileImageKey;

        @Schema(example = "https://bucket.s3.ap-northeast-2.amazonaws.com/profile/...",
                description = "S3 presigned 조회 URL (10분 유효)")
        private String profileImageUrl;
    }

}
