package com.offnal.shifterz.domain.oauth;


import com.offnal.shifterz.domain.member.dto.AuthResponseDto;
import com.offnal.shifterz.domain.oauth.apple.AppleLoginRequestDto;
import com.offnal.shifterz.domain.oauth.kakao.KakaoLoginRequestDto;
import com.offnal.shifterz.global.exception.ErrorApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "소셜 로그인", description = "소셜 로그인 콜백 및 사용자 정보 반환 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class OauthLoginController {

    private final LoginService loginService;

    @Operation(
            summary = "소셜 로그인",
            description = """
                    ---
                    
                    # Social Login
                    
                    provider 값에 따라 Kakao 또는 Apple 로그인을 처리합니다.
                    
                    ## 요청 형식
                    
                    **Kakao**
                    ```json
                    {
                      "provider": "KAKAO",
                      "token": "<Kakao SDK accessToken>"
                    }
                    ```
                    
                    **Apple**
                    ```json
                    {
                      "provider": "APPLE",
                      "token": "<Apple SDK identityToken>",
                      "authorizationCode": "...",
                      "email": "...",
                      "fullName": { "givenName": "...", "familyName": "..." }
                    }
                    ```
                    
                    Apple의 경우 최초 로그인 시에만 email, fullName이 전달됩니다.
                    이후 요청에서는 null로 보내도 기존 회원 정보로 처리됩니다.
                    
                    ---
                    """
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(loginService.login(dto));
    }
}

