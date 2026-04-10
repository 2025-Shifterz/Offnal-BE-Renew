package com.offnal.shifterz.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "INTERNAL_SERVER_ERROR")
    private final String code;

    @Schema(description = "에러 메시지", example = "서버 내부 오류가 발생했습니다.")
    private final String message;


    @Schema(description = "필드별 에러 정보", example = "{\"organizationName\": \"조직 이름은 필수입니다.\"}")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, String> errors;

    public static ErrorResponse from(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

    public ErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.errors = null;
    }

    public static ErrorResponse of(ErrorReason reason) {
        return ErrorResponse.builder()
                .code(reason.getCode())
                .message(reason.getMessage())
                .build();
    }

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ErrorResponse of(String code, String message, Map<String, String> errors) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }
}