package com.offnal.shifterz.domain.member.exception;

import com.offnal.shifterz.global.exception.ErrorCode;

import lombok.Getter;

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
	MEMBER_WITHDRAW_FAILED("회원 탈퇴 처리에 실패했습니다.");

	private final String message;

	MemberErrorCode(String message) {
		this.message = message;
	}
}
