package com.offnal.shifterz.domain.oauth;


import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.dto.AuthResponseDto;
import com.offnal.shifterz.domain.oauth.apple.AppleLoginRequest;
import com.offnal.shifterz.domain.oauth.kakao.KakaoLoginRequest;
import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.exception.ErrorResponse;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.global.util.AuthResponseUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "소셜 로그인", description = "소셜 로그인 콜백 및 사용자 정보 반환 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class OauthLoginController {

    private final LoginService loginService;


    @Operation(summary = "카카오 로그인 콜백 처리", description = "카카오 인가 코드(code)를 기반으로 JWT 토큰과 사용자 정보를 반환합니다.")
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.Member
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class),
                            examples = {
                                    @ExampleObject(name = "기존 회원 로그인", value = """
                                            {"code":"LOGIN_SUCCESS",
                                            "message":"로그인을 성공했습니다.",
                                            "data":{
                                                "nickname":"offnal",
                                                "newMember":false
                                                }
                                               }
                                            """),
                                    @ExampleObject(name = "신규 회원 로그인", value = """
                                             {"code":"LOGIN_SUCCESS",
                                            "message":"로그인을 성공했습니다.",
                                            "data":{
                                                "nickname":"offnal",
                                                "newMember":true
                                                }
                                               }
                                            """)
                            }),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Authorization",
                                    description = "Bearer {accessToken}",
                                    schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Refresh-Token",
                                    description = "Refresh Token",
                                    schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                            )
                    }),
            @ApiResponse(responseCode = "500", description = "회원 등록 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "MEMBER_SAVE_FAILED", value = """
                                    {
                                      "code": "MEMBER_SAVE_FAILED",
                                      "message": "회원 등록에 실패했습니다."
                                    }
                                    """)
                    ))
    })
    @Hidden
    @GetMapping("/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        AuthResponseDto dto = loginService.loginWithSocial(Provider.KAKAO, code);
        return AuthResponseUtil.buildAuthResponse(dto);
    }

    @Operation(
            summary = "애플 로그인 (네이티브)",
            description = """
                    ---
                    
                    # Apple Native Login
                    
                    iOS 또는 React Native에서 전달된 identityToken(JWT)을 이용하여  
                    애플 로그인 또는 신규 회원가입을 처리하는 API입니다.
                    
                    ---
                    
                    ## 인증 처리 흐름
                    
                    1) identityToken 검증  
                    - Apple 공개키(JWK)를 사용하여 RS256 서명을 검증합니다.  
                    - 토큰 내부에서 sub(사용자 고유 식별자), email 정보를 파싱합니다.
                    
                    2) 회원 조회 또는 가입  
                    - provider = APPLE, providerId = sub 조건으로 기존 회원을 조회합니다.  
                    - 이미 회원이 존재하면 로그인 처리됩니다.  
                    - 존재하지 않으면 새 회원을 자동 생성하여 가입 처리됩니다.
                    
                    3) Access / Refresh Token 발급  
                    - 로그인 또는 신규 가입 후 서버에서 JWT 토큰을 발급합니다.
                    
                    ---
                    
                    ## Apple 개인정보 제공 정책 안내
                    
                    Apple은 최초 로그인 시에만 email, fullName(이름) 정보를 전달할 수 있습니다.  
                    이후 로그인 요청에서는 해당 정보가 전달되지 않아도,  
                    서버는 기존 저장된 회원 정보를 기반으로 정상적으로 로그인 처리를 수행합니다.
                    
                    ---
                    
                    ## 요청 데이터 안내
                    
                    클라이언트는 로그인 시마다 identityToken만 전송하면 됩니다.  
                    email 및 fullName 정보를 null로 요청 시, 서버는 기존 회원 정보를 사용합니다.  
        
                    ## 응답 데이터 구성
                    
                    서버는 다음 정보를 포함하여 응답합니다:
                    
                    - 회원 기본 정보  
                    - 신규 가입 여부 (newMember = true/false)  
                    - Access Token  
                    - Refresh Token  
                    
                    ---
                    """
    )

    @ErrorApiResponses.AppleLoginError
    @PostMapping("/login/apple")
    public SuccessResponse<AuthResponseDto> appleNativeLogin(
            @RequestBody AppleLoginRequest request
    ) {
        AuthResponseDto response = loginService.loginWithAppleNative(request);
        return SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, response);
    }

    @Operation(
            summary = "카카오 로그인 (네이티브)",
            description = """
                     Kakao Native SDK를 통해 획득한 accessToken을 이용하여 카카오 로그인 또는 신규 회원가입을 처리하는 API입니다.
                    """
    )
    @PostMapping("/login/kakao")
    public SuccessResponse<AuthResponseDto> kakaoNativeLogin(
            @RequestBody @Valid KakaoLoginRequest request
    ){
        AuthResponseDto response = loginService.loginWithKakaoNative(request);
        return SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, response);
    }
}

