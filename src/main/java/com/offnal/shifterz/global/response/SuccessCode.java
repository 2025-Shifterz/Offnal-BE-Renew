package com.offnal.shifterz.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {

	LOGIN_SUCCESS("AUTH001", HttpStatus.OK, "로그인을 성공했습니다."),
	LOGOUT_SUCCESS("AUTH002", HttpStatus.OK, "로그아웃에 성공했습니다."),
	MEMBER_DELETED("AUTH003", HttpStatus.OK, "회원 탈퇴에 성공했습니다."),
	DATA_FETCHED("COMMON001", HttpStatus.OK, "데이터 조회에 성공했습니다."),
	OK("COMMON002", HttpStatus.OK, "요청이 정상적으로 처리되었습니다."),
	TOKEN_REISSUED("AUTH200", HttpStatus.OK, "토큰이 재발급되었습니다."),

	CALENDAR_CREATED("CAL001", HttpStatus.OK, "근무표 등록에 성공했습니다."),
	CALENDAR_UPDATED("CAL002", HttpStatus.OK, "근무표 수정에 성공했습니다."),
	CALENDAR_DELETED("CAL003", HttpStatus.OK, "근무표 삭제에 성공했습니다."),
	WORK_INSTANCES_DELETED("CAL003", HttpStatus.OK, "근무 일정 삭제에 성공했습니다."),
	CALENDAR_DATA_FETCHED("CAL005", HttpStatus.OK, "캘린더 정보를 조회했습니다."),
	WORK_DAY_FETCHED("CAL006", HttpStatus.OK, "근무일 조회에 성공했습니다."),
	WORK_TIME_UPDATED("CAL007", HttpStatus.OK, "근무 시간 수정에 성공했습니다."),

	TODO_CREATED("TODO201", HttpStatus.CREATED, "할 일이 생성되었습니다."),
	TODO_UPDATED("TODO200", HttpStatus.OK, "할 일이 수정되었습니다."),
	TODO_FETCHED("TODO200", HttpStatus.OK, "할 일을 조회했습니다."),
	TODO_DELETED("TODO204", HttpStatus.NO_CONTENT, "할 일이 삭제되었습니다."),
	TODO_LIST_FETCHED("TODO004", HttpStatus.OK, "할 일 목록을 성공적으로 조회했습니다."),

	PROFILE_UPDATED("MEM001", HttpStatus.OK, "프로필 수정에 성공했습니다."),
	MY_INFO_FETCHED("MEM002", HttpStatus.OK, "내 정보 조회에 성공했습니다."),
	PROFILE_UPLOAD_URL_CREATED("MEM003", HttpStatus.OK, "S3용 프로필 사진 업로드 url 생성을 성공했습니다."),
	PROFILE_IMAGE_DELETED("MEM004", HttpStatus.OK, "프로필 이미지가 삭제되었습니다."),

	MEMO_CREATED("MEMO001", HttpStatus.CREATED, "메모가 성공적으로 생성되었습니다."),
	MEMO_UPDATED("MEMO002", HttpStatus.OK, "메모가 성공적으로 수정되었습니다."),
	MEMO_FETCHED("MEMO003", HttpStatus.OK, "메모가 성공적으로 조회되었습니다."),
	MEMO_DELETED("MEMO004", HttpStatus.OK, "메모가 성공적으로 삭제되었습니다."),
	MEMO_LIST_FETCHED("MEMO200", HttpStatus.OK, "메모 목록이 성공적으로 조회되었습니다."),

	ORGANIZATION_CREATED("ORG001", HttpStatus.CREATED, "조직이 성공적으로 생성되었습니다."),
	ORGANIZATION_UPDATED("ORG002", HttpStatus.OK, "조직이 성공적으로 수정되었습니다."),
	ORGANIZATION_FETCHED("ORG003", HttpStatus.OK, "조직이 성공적으로 조회되었습니다."),
	ORGANIZATION_DELETED("ORG004", HttpStatus.OK, "조직이 성공적으로 삭제되었습니다."),

	WORK_SCHEDULE_FETCHED("HOME001", HttpStatus.OK, "근무 일정을 성공적으로 조회했습니다."),
	DAILY_ROUTINE_FETCHED("HOME002", HttpStatus.OK, "루틴을 성공적으로 조회했습니다."),

	BEDROCK_VISION_SUCCESS("BRK200", HttpStatus.OK, "Bedrock 이미지 분석 성공");

	private final String code;
	private final HttpStatus httpStatus;
	private final String message;

	SuccessCode(String code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
