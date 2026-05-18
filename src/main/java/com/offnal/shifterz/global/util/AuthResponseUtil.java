package com.offnal.shifterz.global.util;

import com.offnal.shifterz.domain.member.dto.AuthResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class AuthResponseUtil {

    public static ResponseEntity<?> buildAuthResponse(AuthResponseDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + dto.getAccessToken());
        headers.set("Refresh-Token", dto.getRefreshToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ResponseEntity.ok(dto));
    }
}
