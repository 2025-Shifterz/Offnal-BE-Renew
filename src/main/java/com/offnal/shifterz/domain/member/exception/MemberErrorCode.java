package com.offnal.shifterz.domain.member.exception;

import com.offnal.shifterz.global.exception.ErrorCode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements ErrorCode {

	// 기본
	MEMBER_NOT_FOUND("회원을 찾을 수 없습니다."),
	MEMBER_ALREADY_EXISTS("이미 존재하는 회원입니다."),

	// 인증/인가 관련
	MEMBER_ACCESS_DENIED("회원 접근 권한이 없습니다."),
	UNAUTHORIZED_MEMBER("인증되지 않은 사용자입니다."),

	// 요청 관련
	INVALID_MEMBER_REQUEST("잘못된 회원 요청입니다."),
	INVALID_MEMBER_ID("유효하지 않은 회원 ID입니다."),

	// 처리 실패
	MEMBER_SAVE_FAILED("회원 저장에 실패했습니다."),
	MEMBER_WITHDRAW_FAILED("회원 탈퇴 처리에 실패했습니다."),

	// S3
	S3_UPLOAD_FAILED("프로필 사진을 S3 업로드 실패하였습니다."),
	S3_DELETE_FAILED("S3에 업로드된 프로필 사진을 삭제하는 데에 실패하였습니다."),
	S3_KEY_ALREADY_EXISTS("이미 프로필 이미지 Key가 존재하는 회원입니다."),
	S3_KEY_NOT_FOUND("존재하지 않는 S3 Key입니다."),
	UPLOAD_TO_S3_FAILED("S3에 사진 업로드를 실패하였습니다."),
	UNSUPPORTED_CONTENT_TYPE("지원하지 않는 이미지 파일 확장자입니다.");
	;

	private final String message;

	MemberErrorCode(String message) {
		this.message = message;
	}
}
