package com.offnal.shifterz.domain.member.service;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.exception.MemberErrorCode;
import com.offnal.shifterz.domain.oauth.apple.AppleOAuthHandler;
import com.offnal.shifterz.global.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberWithdrawFacade {

    private final MemberService memberService;
    private final AppleOAuthHandler appleOAuthHandler;

    public void withdraw(HttpServletRequest request) {
        Member member = memberService.getCurrentMemberEntity();

        revokeAppleIfNecessary(member);

        memberService.executeWithdraw(member, request);
    }

    private void revokeAppleIfNecessary(Member member) {
        if (member.getProvider() != Provider.APPLE) {
            return;
        }
        try {
            appleOAuthHandler.revoke(member);
        } catch (Exception e) {
            throw new CustomException(MemberErrorCode.MEMBER_WITHDRAW_FAILED);
        }
    }
}