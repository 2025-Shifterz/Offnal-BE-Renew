package com.offnal.shifterz;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.offnal.shifterz.core.jwt.CustomUserDetails;
import com.offnal.shifterz.core.jwt.CustomUserDetailsService;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.exception.MemberErrorCode;
import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.global.exception.CustomException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	@Test
	void 회원_ID로_사용자_조회_성공() {
		// given
		Long memberId = 1L;

		Member member = Member.builder()
			.id(memberId)
			.memberName("테스트유저")
			.build();

		when(memberRepository.findById(memberId))
			.thenReturn(Optional.of(member));

		// when
		CustomUserDetails result = customUserDetailsService.loadByMemberId(memberId);

		// then
		assertThat(result.getId()).isEqualTo(memberId);
		assertThat(result.getMember()).isEqualTo(member);
	}

	@Test
	void 회원이_없으면_예외_발생() {
		// given
		Long memberId = 1L;

		when(memberRepository.findById(memberId))
			.thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(
			CustomException.class,
			() -> customUserDetailsService.loadByMemberId(memberId)
		);

		assertThat(exception.getErrorCode())
			.isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
	}
}
