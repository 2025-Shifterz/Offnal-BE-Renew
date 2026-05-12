package com.offnal.shifterz.core.jwt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.exception.MemberErrorCode;
import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public CustomUserDetails loadByMemberId(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

		return new CustomUserDetails(member);
	}
}
