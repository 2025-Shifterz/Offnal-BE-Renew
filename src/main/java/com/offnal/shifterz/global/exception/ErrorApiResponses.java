package com.offnal.shifterz.global.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface ErrorApiResponses {

    //공통 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "INTERNAL_SERVER_ERROR", value = """
                                    {
                                      "code": "INTERNAL_SERVER_ERROR",
                                      "message": "서버 내부 오류가 발생했습니다."
                                    }
                                    """)
                    ))
    })
    @interface Common {}

   //인증 관련 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "유효하지 않은 카카오 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "INVALID_KAKAO_TOKEN", value = """
                                    {
                                      "code": "INVALID_KAKAO_TOKEN",
                                      "message": "유효하지 않은 카카오 액세스 토큰입니다."
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "401", description = "로그아웃된 Access Token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "LOGOUT_TOKEN", value = """
                                {
                                  "code": "LOGOUT_TOKEN",
                                  "message": "이미 로그아웃된 토큰입니다."
                                }
                                """)
                    )),
            @ApiResponse(responseCode = "502", description = "카카오 사용자 정보 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "KAKAO_USERINFO_FETCH_FAILED", value = """
                                    {
                                      "code": "KAKAO_USERINFO_FETCH_FAILED",
                                      "message": "카카오 사용자 정보 조회에 실패했습니다."
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "FORBIDDEN", value = """
                                    {
                                      "code": "FORBIDDEN",
                                      "message": "접근이 거부되었습니다."
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "500", description = "회원 탈퇴 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "MEMBER_WITHDRAW_FAILED", value = """
                                    {
                                      "code": "MEMBER_WITHDRAW_FAILED",
                                      "message": "회원 탈퇴에 실패했습니다."
                                    }
                                    """)
                    ))
    })
    @interface Auth {}

    //회원 관련 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "MEMBER_NOT_FOUND", value = """
                                    {
                                      "code": "MEMBER_NOT_FOUND",
                                      "message": "존재하지 않는 회원입니다."
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "500", description = "회원 저장 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "MEMBER_SAVE_FAILED", value = """
                                    {
                                      "code": "MEMBER_SAVE_FAILED",
                                      "message": "회원 저장에 실패했습니다."
                                    }
                                    """)
                    ))
    })
    @interface Member {}

    // 캘린더 생성 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "근무표 등록 요청 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "CALENDAR_NAME_REQUIRED", value = """
                                        {
                                          "code": "CALENDAR_NAME_REQUIRED",
                                          "message": "근무표 이름은 필수입니다."
                                        }
                                        """),
                                    @ExampleObject(name = "CALENDAR_YEAR_REQUIRED", value = """
                                        {
                                          "code": "CALENDAR_YEAR_REQUIRED",
                                          "message": "연도는 필수입니다."
                                        }
                                        """),
                                    @ExampleObject(name = "CALENDAR_MONTH_REQUIRED", value = """
                                        {
                                          "code": "CALENDAR_MONTH_REQUIRED",
                                          "message": "월은 필수입니다."
                                        }
                                        """),
                                    @ExampleObject(name = "CALENDAR_WORK_GROUP_REQUIRED", value = """
                                        {
                                          "code": "CALENDAR_WORK_GROUP_REQUIRED",
                                          "message": "근무조는 필수입니다."
                                        }
                                        """),
                                    @ExampleObject(name = "CALENDAR_WORK_TIME_REQUIRED", value = """
                                        {
                                          "code": "CALENDAR_WORK_TIME_REQUIRED",
                                          "message": "근무 시간 정보는 필수입니다."
                                        }
                                        """),
                                    @ExampleObject(name = "CALENDAR_SHIFT_REQUIRED", value = """
                                        {
                                          "code": "CALENDAR_SHIFT_REQUIRED",
                                          "message": "근무일 정보는 필수입니다."
                                        }
                                        """),
                                    @ExampleObject(name = "CALENDAR_DUPLICATION", value = """
                                        {
                                          "code": "CALENDAR_DUPLICATION",
                                          "message": "이미 존재하는 조직의 캘린더입니다."
                                        }
                                        """),
                                    @ExampleObject(name = "CALENDAR_START_TIME_INVALID", value = """
                                        {
                                          "code": "CALENDAR_START_TIME_INVALID",
                                          "message": "시작 시간이 유효하지 않습니다."
                                        }
                                        """),
                                    @ExampleObject(name = "CALENDAR_DURATION_REQUIRED", value = """
                                        {
                                          "code": "CALENDAR_DURATION_REQUIRED",
                                          "message": "근무 소요 시간은 필수입니다."
                                        }
                                        """),
                                    @ExampleObject(name = "INVALID_SHIFT_DATE", value = """
                                        {
                                          "code": "INVALID_SHIFT_DATE",
                                          "message": "근무 일정이 캘린더의 범위를 벗어났습니다."
                                        }
                                        """)
                            }
                    )
            ),
            @ApiResponse(responseCode = "404",  description = "근무표 등록 요청 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "CALENDAR_ORGANIZATION_NOT_FOUND", value = """
                                        {
                                          "code": "CALENDAR_ORGANIZATION_NOT_FOUND",
                                          "message": "존재하지 않는 조직입니다."
                                        }
                                        """)
                            }
                    )
            )
    })
    @interface CreateWorkCalendar {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404",  description = "근무표 수정 요청 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "CALENDAR_NOT_FOUND", value = """
                                        {
                                          "code": "CALENDAR_NOT_FOUND",
                                          "message": "해당하는 캘린더를 찾을 수 없습니다."
                                        }
                                        """)
                            }
                    ))
    })
    @interface UpdateWorkCalendar {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404",  description = "근무표 삭제 요청 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "CALENDAR_NOT_FOUND", value = """
                                        {
                                          "code": "CALENDAR_NOT_FOUND",
                                          "message": "해당하는 캘린더를 찾을 수 없습니다."
                                        }
                                        """)
                            }
                    )),
            @ApiResponse(responseCode = "500",  description = "근무표 삭제 요청 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "CALENDAR_DELETE_FAILED", value = """
                                        {
                                          "code": "CALENDAR_DELETE_FAILED",
                                          "message": "근무표 삭제에 실패하였습니다."
                                        }
                                        """)
                            }
                    ))
    })
    @interface DeleteWorkCalendar {}

    // 근무일 조회 관련 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식 (연도/월)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_YEAR_FORMAT", value = """
                                    {
                                      "code": "INVALID_YEAR_FORMAT",
                                      "message": "연도 형식이 올바르지 않습니다."
                                    }
                                    """),
                                    @ExampleObject(name = "INVALID_MONTH_FORMAT", value = """
                                    {
                                      "code": "INVALID_MONTH_FORMAT",
                                      "message": "월 형식이 올바르지 않습니다."
                                    }
                                    """),
                                    @ExampleObject(name = "CALENDAR_DATE_REQUIRED", value = """
                                    {
                                      "code": "CALENDAR_DATE_REQUIRED",
                                      "message": "기간을 입력해주세요."
                                    }
                                    """),
                                    @ExampleObject(name = "CALENDAR_INVALID_DATE_RANGE", value = """
                                    {
                                      "code": "CALENDAR_INVALID_DATE_RANGE",
                                      "message": "기간 범위가 올바르지 않습니다."
                                    }
                                    """)
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "근무일 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "WORK_INSTANCE_NOT_FOUND", value = """
                                    {
                                    "code": "WORK_INSTANCE_NOT_FOUND",
                                    "message": "해당 연도와 월에 대한 근무일이 존재하지 않습니다."
                                    }
                                    """),
                                    @ExampleObject(name = "WORK_TIME_NOT_FOUND", value = """
                                    {
                                    "code": "WORK_TIME_NOT_FOUND",
                                    "message": "오늘의 근무 시간 정보가 없습니다."
                                    }
                                    """)
                            }
                    ))
    })
    @interface WorkDay {}

    // 조직 관련 에러

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 필드",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ORGANIZATION_NOT_VALIDATE", value = """
                                    {
                                      "code": "ORG005",
                                      "message": "유효하지 않은 필드입니다."
                                    }
                                    """
                            )
                    )),
            @ApiResponse(responseCode = "403", description = "조직 접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ORGANIZATION_ACCESS_DENIED", value = """
                                    {
                                      "code": "ORG003",
                                      "message": "해당 조직에 접근 권한이 없습니다."
                                    }
                                    """
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "조직을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ORGANIZATION_NOT_FOUND", value = """
                                    {
                                      "code": "ORG001",
                                      "message": "소속 조직을 찾을 수 없습니다."
                                    }
                                    """
                            )
                    )),
            @ApiResponse(responseCode = "409", description = "조직 중복(동일 이름/팀)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ORGANIZATION_DUPLICATE_NAME", value = """
                                    {
                                      "code": "ORG004",
                                      "message": "동일한 이름/팀의 조직이 이미 존재합니다."
                                    }
                                    """
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "조직 저장/갱신/삭제 실패(내부 오류)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ORGANIZATION_SAVE_FAILED", value = """
                                    {
                                      "code": "ORG002",
                                      "message": "조직 저장에 실패했습니다."
                                    }
                                    """
                            )
                    ))
    })
    @interface Organization {}

    // S3 관련 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복 Key",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "S3_KEY_ALREADY_EXISTS", value = """
                                            {
                                              "code": "S3_KEY_ALREADY_EXISTS",
                                              "message": "이미 프로필 이미지가 존재합니다."
                                            }
                                            """)
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "잘못된 요청 또는 중복 Key",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "S3_KEY_NOT_FOUND", value = """
                                            {
                                              "code": "S3_KEY_NOT_FOUND",
                                              "message": "해당 S3 객체 키를 찾을 수 없습니다."
                                            }
                                            """)
                            }
                    )),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 확장자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "UNSUPPORTED_CONTENT_TYPE", value = """
                                            {
                                              "code": "UNSUPPORTED_CONTENT_TYPE",
                                              "message": "지원하지 않는 이미지 파일 확장자입니다."
                                            }
                                            """)
                            }
                    )),
            @ApiResponse(responseCode = "500", description = "S3 업로드/삭제 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "S3_UPLOAD_FAILED", value = """
                                            {
                                              "code": "S3_UPLOAD_FAILED",
                                              "message": "S3 파일 업로드에 실패했습니다."
                                            }
                                            """),
                                    @ExampleObject(name = "S3_DELETE_FAILED", value = """
                                            {
                                              "code": "S3_DELETE_FAILED",
                                              "message": "S3 파일 삭제에 실패했습니다."
                                            }
                                            """),
                                    @ExampleObject(name = "UPLOAD_TO_S3_FAILED", value = """
                                            {
                                              "code": "UPLOAD_TO_S3_FAILED",
                                              "message": "S3에 사진 업로드를 실패하였습니다."
                                            }
                                            """)
                            }
                    ))
    })
    @interface S3 {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 Apple identity token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "APPLE_TOKEN_INVALID",
                                    value = """
                                {
                                  "code": "APL001",
                                  "message": "유효하지 않은 Apple identity token입니다."
                                }
                                """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "kid에 해당하는 Apple 공개키 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "APPLE_PUBLIC_KEY_NOT_FOUND",
                                    value = """
                                {
                                  "code": "APL002",
                                  "message": "kid에 해당하는 Apple 공개키를 찾을 수 없습니다."
                                }
                                """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Apple 공개키 처리 중 오류 발생",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "APPLE_PUBLIC_KEY_ERROR",
                                    value = """
                                {
                                  "code": "APL003",
                                  "message": "Apple 공개키 처리 중 오류가 발생했습니다."
                                }
                                """
                            )
                    )
            )
    })
    public @interface AppleLoginError { }

}
