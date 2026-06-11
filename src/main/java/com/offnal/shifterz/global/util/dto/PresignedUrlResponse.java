package com.offnal.shifterz.global.util.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PresignedUrlResponse {

    @Schema(description = "S3에 업로드할 때 사용할 presigned URL")
    private String uploadUrl;

    @Schema(description = "업로드될 파일의 S3 key")
    private String key;
}
