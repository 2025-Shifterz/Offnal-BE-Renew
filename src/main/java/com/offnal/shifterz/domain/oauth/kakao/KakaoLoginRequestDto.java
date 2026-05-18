package com.offnal.shifterz.domain.oauth.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class KakaoLoginRequestDto {
    @NotBlank(message = "accessToken은 필수입니다.")
    @Schema(description = "Kakao에서 받은 accessToken")
    private String accessToken;
}
