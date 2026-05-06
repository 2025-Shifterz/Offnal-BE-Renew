package com.offnal.shifterz.domain.oauth.apple;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppleLoginRequest {

    @Schema(description = "iOS 네이티브 로그인에서 받은 identityToken(JWT)", required = true)
    private String identityToken;

    @Schema(description = "Authorization Code", required = true)
    private String authorizationCode;

    @Schema(description = "Apple 고유 사용자 ID (user identifier)")
    private String user;

    @Schema(description = "Apple에서 제공하는 이메일(혹은 private email)")
    private String email;

    @Schema(description = "최초 로그인 시에만 제공되는 이름 정보")
    private FullName fullName;


    @Data
    @NoArgsConstructor
    public static class FullName {

        @Schema(description = "이름(given name)")
        private String givenName;

        @Schema(description = "성(family name)")
        private String familyName;

        @JsonIgnore
        public String getFullName() {
            if (givenName == null && familyName == null) {
                return null;
            }
            return (familyName != null ? familyName : "")
                    + (givenName != null ? givenName : "");
        }
    }
}
