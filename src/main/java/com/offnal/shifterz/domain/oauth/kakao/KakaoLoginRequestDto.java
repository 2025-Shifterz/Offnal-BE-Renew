package com.offnal.shifterz.domain.oauth.kakao;

import com.offnal.shifterz.domain.oauth.LoginRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class KakaoLoginRequestDto extends LoginRequestDto {
//    @NotBlank(message = "accessToken은 필수입니다.")
//    @Schema(description = "Kakao에서 받은 accessToken")
//    private String accessToken;
}
