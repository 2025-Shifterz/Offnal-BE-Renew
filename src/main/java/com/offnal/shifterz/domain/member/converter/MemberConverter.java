package com.offnal.shifterz.domain.member.converter;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.dto.MemberResponseDto;
import com.offnal.shifterz.global.util.encrypt.EncryptUtil;

public class MemberConverter {

    public static MemberResponseDto.MemberRegisterResponseDto toRegisterResponse(Member member, boolean isNewMember,
                                                                                 EncryptUtil encryptUtil) {
        return MemberResponseDto.MemberRegisterResponseDto.builder()
                .id(member.getId())
                .email(encryptUtil.decryptAESOrNull(member.getEmail()))
                .memberName(encryptUtil.decryptAESOrNull(member.getMemberName()))
                .phoneNumber(encryptUtil.decryptAESOrNull(member.getPhoneNumber()))
                .profileImageKey(member.getProfileImageKey())
                .isNewMember(isNewMember)
                .build();
    }

    public static MemberResponseDto.MemberUpdateResponseDto toMyInfoResponse(Member member, String profileImageUrl,
                                                                             EncryptUtil encryptUtil) {
        String key = member.getProfileImageKey();

        return MemberResponseDto.MemberUpdateResponseDto.builder()
                .id(member.getId())
                .email(encryptUtil.decryptAESOrNull(member.getEmail()))
                .memberName(encryptUtil.decryptAESOrNull(member.getMemberName()))
                .phoneNumber(encryptUtil.decryptAESOrNull(member.getPhoneNumber()))
                .profileImageKey(key)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}

