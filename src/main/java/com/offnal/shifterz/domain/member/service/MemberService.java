package com.offnal.shifterz.domain.member.service;

import com.offnal.shifterz.core.jwt.TokenService;
import com.offnal.shifterz.domain.log.domain.Log;
import com.offnal.shifterz.domain.log.repository.LogRepository;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.dto.MemberRequestDto;
import com.offnal.shifterz.domain.member.dto.MemberResponseDto;
import com.offnal.shifterz.domain.member.exception.MemberErrorCode;
import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.domain.memberOrganizationTeam.repository.MemberOrganizationTeamRepository;
import com.offnal.shifterz.domain.memo.repository.MemoRepository;
import com.offnal.shifterz.domain.organization.repository.OrganizationRepository;
import com.offnal.shifterz.domain.todo.repository.TodoRepository;
import com.offnal.shifterz.domain.work.repository.WorkCalendarRepository;
import com.offnal.shifterz.domain.work.repository.WorkInstanceRepository;
import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.util.RedisUtil;
import com.offnal.shifterz.global.util.S3Service;
import com.offnal.shifterz.global.util.dto.PresignedUrlResponse;
import com.offnal.shifterz.global.util.encrypt.EncryptUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final MemoRepository memoRepository;
    private final TodoRepository todoRepository;
    private final OrganizationRepository organizationRepository;
    private final LogRepository logRepository;
    private final RedisUtil redisUtil;
    private final WorkInstanceRepository workInstanceRepository;
    private final WorkCalendarRepository workCalendarRepository;
    private final MemberOrganizationTeamRepository memberOrganizationTeamRepository;
    private final TokenService tokenService;
    private final EncryptUtil encryptUtil;

    /**
     * 소셜 로그인 회원 등록 또는 업데이트
     * @param provider 소셜 제공자 (KAKAO, GOOGLE, NAVER ...)
     * @param providerId 제공자 ID
     * @param email 이메일
     * @param memberName 이름
     * @param phoneNumber 전화번호
     * @return 등록/업데이트된 Member와 신규 가입 여부
     */
    @Transactional
    public MemberResponseDto.MemberRegisterResponseDto registerMemberIfAbsent(
            Provider provider,
            String providerId,
            String email,
            String memberName,
            String phoneNumber,
            String socialProfileImageUrl,
            String appleRefreshToken
    ) {
        Optional<Member> existingMember = memberRepository.findByProviderAndProviderId(provider, providerId);

        if (existingMember.isPresent()) {
            Member member = existingMember.get();

            // 기존 회원이어도 refresh_token 새로 받으면 업데이트
            if (provider == Provider.APPLE && appleRefreshToken != null) {
                member.updateAppleRefreshToken(encryptUtil.encryptAESOrNull(appleRefreshToken)); // ✅
            }

            return MemberResponseDto.MemberRegisterResponseDto.from(member, false, encryptUtil);
        }

        // 신규 회원 생성
        Member newMember = Member.builder()
                .provider(provider)
                .providerId(providerId)
                .email(encryptUtil.encryptAESOrNull(email))
                .memberName(encryptUtil.encryptAESOrNull(memberName))
                .phoneNumber(encryptUtil.encryptAESOrNull(phoneNumber))
                .appleRefreshToken(provider == Provider.APPLE
                        ? encryptUtil.encryptAESOrNull(appleRefreshToken)
                        : null
                )
                .profileImageKey(null)
                .build();

        Member savedMember = memberRepository.save(newMember);

        // 프로필 이미지 저장
        if (socialProfileImageUrl != null && !socialProfileImageUrl.isBlank()) {
            uploadSocialProfileImage(savedMember, socialProfileImageUrl);
        }

        return MemberResponseDto.MemberRegisterResponseDto.from(savedMember, true, encryptUtil);

    }

    private void uploadSocialProfileImage(Member member, String socialProfileImageUrl) {
        try {
            String extension = extractExtensionFromUrl(socialProfileImageUrl);

            String key = "profile/" + UUID.randomUUID() + "." + extension;

            byte[] bytes = s3Service.downloadImageFromUrl(socialProfileImageUrl);

            s3Service.uploadImageBytes(bytes, key);

            member.updateMemberInfo(
                    member.getMemberName(),
                    key
            );
        } catch (Exception e) {
            throw new CustomException(MemberErrorCode.UPLOAD_TO_S3_FAILED);
        }
    }

    private String extractExtensionFromUrl(String url) {
        try {
            String lower = url.toLowerCase();

            if (lower.contains(".png"))
                return "png";
            if (lower.contains(".jpeg"))
                return "jpeg";
            if (lower.contains(".jpg"))
                return "jpg";

            throw new CustomException(MemberErrorCode.UNSUPPORTED_CONTENT_TYPE);
        } catch (Exception e) {
            throw new CustomException(MemberErrorCode.UNSUPPORTED_CONTENT_TYPE);
        }
    }

    /**
     * 내 프로필 조회
     */
    @Transactional
    public MemberResponseDto.MemberUpdateResponseDto getMyInfo() {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        String key = member.getProfileImageKey();
        String presignedUrl = null;

        if (key != null && !key.isEmpty()) {
            presignedUrl = s3Service.generateViewPresignedUrl(key);
        }

        return MemberResponseDto.MemberUpdateResponseDto.from(member, presignedUrl, encryptUtil);
    }

    /**
     * Facade에서 사용 - 현재 인증된 회원 엔티티 반환
     */
    @Transactional
    public Member getCurrentMemberEntity() {
        Long memberId = AuthService.getCurrentUserId();
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 회원 탈퇴 - Apple revoke는 MemberWithdrawFacade에서 선처리 후 호출
     */
    @Transactional
    public void executeWithdraw(Member member, HttpServletRequest request) {
        Long memberId = member.getId();
        String anonymized = "deleted_user_" + UUID.randomUUID();

        try {
            String profileImageKey = member.getProfileImageKey();
            if (profileImageKey != null && !profileImageKey.isEmpty()) {
                s3Service.deleteFile(profileImageKey);
            }

            logRepository.anonymizeMemberLogs(memberId, anonymized);

            memoRepository.deleteByMemberId(memberId);
            todoRepository.deleteByMemberId(memberId);
            workInstanceRepository.deleteByMemberId(memberId);
            workCalendarRepository.deleteByMemberId(memberId);
            memberOrganizationTeamRepository.deleteAllByMemberId(memberId);

            organizationRepository.deleteByOrganizationMember_Id(memberId);

            memberRepository.deleteById(memberId);

            redisUtil.delete("RT:" + memberId);

            String accessToken = request.getHeader("Authorization").substring(7);
            tokenService.blacklistAccessToken(accessToken);

            Log withdrawLog = Log.builder()
                    .member(null)
                    .action('C')
                    .time(LocalDateTime.now())
                    .message("회원 탈퇴 처리 완료")
                    .anonymizedIdentifier(anonymized)
                    .build();
            logRepository.save(withdrawLog);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(MemberErrorCode.MEMBER_WITHDRAW_FAILED);
        }
    }

    /**
     * 일반 정보 수정
     */
    @Transactional
    public MemberResponseDto.MemberUpdateResponseDto updateMemberInfo(MemberRequestDto.MemberUpdateRequestDto request) {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateMemberInfo(
                encryptUtil.encryptAESOrNull(request.getName()),
                member.getProfileImageKey()
        );

        String key = member.getProfileImageKey();
        String presignedUrl = null;

        if (key != null && !key.isEmpty()) {
            presignedUrl = s3Service.generateViewPresignedUrl(key);
        }

        return MemberResponseDto.MemberUpdateResponseDto.from(member, presignedUrl, encryptUtil);
    }

    /**
     * 사진 정보 추가 또는 수정
     */
    @Transactional
    public void updateProfileImage(String newImageKey) {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        String currentKey = member.getProfileImageKey();

        if (currentKey == null || currentKey.isEmpty()) {
            member.updateMemberInfo(
                    member.getMemberName(),
                    newImageKey
            );
        }
    }

    @Transactional
    public void deleteProfileImage() {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        String currentKey = member.getProfileImageKey();

        if (currentKey == null || currentKey.isEmpty()) {
            throw new CustomException(MemberErrorCode.S3_KEY_NOT_FOUND);
        }

        s3Service.deleteFile(currentKey);

        member.updateMemberInfo(
                member.getMemberName(),
                null
        );
    }

    @Transactional
    public PresignedUrlResponse generateProfileUploadUrl(String extension) {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        String existingKey = member.getProfileImageKey();

        PresignedUrlResponse presigned = s3Service.createPresignedUrl(
                extension,
                existingKey
        );

        if (existingKey == null || existingKey.isEmpty()) {
            updateProfileImage(presigned.getKey());
        }

        return presigned;
    }

}
