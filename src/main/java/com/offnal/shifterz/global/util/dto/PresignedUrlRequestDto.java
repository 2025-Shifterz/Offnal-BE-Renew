package com.offnal.shifterz.global.util.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PresignedUrlRequestDto {
    @Schema(description = "업로드할 이미지의 확장자 (예: png, jpg, jpeg)", example = "jpg")
    private String extension;
}
