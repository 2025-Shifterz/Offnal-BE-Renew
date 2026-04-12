package com.offnal.shifterz.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> {

    @Schema(description = "응답 코드", example = "LOGIN_SUCCESS")
    private final String code;

    @Schema(description = "응답 메시지", example = "로그인을 성공했습니다.")
    private final String message;

    @Schema(description = "응답 데이터")
    private final T data;

    public static <T> SuccessResponse<T> success(SuccessCode code, T data) {
        return SuccessResponse.<T>builder()
                .code(code.name())
                .message(code.getMessage())
                .data(data)
                .build();
    }

    public static SuccessResponse<Void> success(SuccessCode code) {
        return SuccessResponse.<Void>builder()
                .code(code.name())
                .message(code.getMessage())
                .data(null)
                .build();
    }


}