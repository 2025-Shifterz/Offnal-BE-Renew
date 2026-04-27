package com.offnal.shifterz.global;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.offnal.shifterz.core.jwt.CustomUserDetails;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.global.exception.CommonErrorCode;
import com.offnal.shifterz.global.exception.CustomException;

@Component
public class AuthContext {

	public Long getCurrentUserId() {
		return getCurrentUserDetails().getId();
	}

	public Member getCurrentMember() {
		return getCurrentUserDetails().getMember();
	}

	private CustomUserDetails getCurrentUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null ||
			!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
			throw new CustomException(CommonErrorCode.UNAUTHORIZED);
		}

		return userDetails;
	}
}