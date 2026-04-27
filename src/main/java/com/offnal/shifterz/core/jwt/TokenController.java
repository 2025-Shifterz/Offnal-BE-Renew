package com.offnal.shifterz.core.jwt;


import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.core.jwt.exception.TokenErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "토큰 재발급", description = "만료된 Access Token을 재발급합니다. Refresh Token이 필요합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto.TokenResponse> reissue(
        @RequestBody TokenDto.TokenReissueRequest request) {

        TokenDto.TokenResponse response = tokenService.reissue(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃", description = "Access Token을 블랙리스트에 등록하고 Refresh Token을 삭제합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        String accessToken = jwtTokenProvider.resolveToken(request);

        if (accessToken == null) {
            throw new CustomException(TokenErrorCode.INVALID_TOKEN);
        }

        tokenService.logout(accessToken);

        return ResponseEntity.ok().build();
    }
}
