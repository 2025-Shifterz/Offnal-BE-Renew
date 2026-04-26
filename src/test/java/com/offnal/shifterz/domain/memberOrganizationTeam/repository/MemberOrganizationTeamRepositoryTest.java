package com.offnal.shifterz.domain.memberOrganizationTeam.repository;

import com.offnal.shifterz.core.config.QueryDslConfig;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.domain.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.organization.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberOrganizationTeamRepositoryTest {

    @Autowired
    private MemberOrganizationTeamRepository memberOrganizationTeamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Member member;
    private Member otherMember;
    private Organization organization;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
                .provider(Provider.KAKAO)
                .providerId("12345678")
                .build());
        otherMember = memberRepository.save(Member.builder()
                .provider(Provider.KAKAO)
                .providerId("11111111")
                .build());
        organization = organizationRepository.save(Organization.builder()
                .organizationName("병원A")
                .team("내과팀")
                .organizationMember(member)
                .build());
    }

    private MemberOrganizationTeam saveMot(Member m, Organization org, String team) {
        return memberOrganizationTeamRepository.save(
                MemberOrganizationTeam.builder()
                        .member(m)
                        .organization(org)
                        .team(team)
                        .build()
        );
    }

    // ===== findMemberOrganizationTeam =====

    @Test
    @DisplayName("memberId와 organization으로 MemberOrganizationTeam을 조회한다")
    void findMemberOrganizationTeam_byMemberIdAndOrganization() {
        // given
        saveMot(member, organization, "내과팀");

        // when
        Optional<MemberOrganizationTeam> result = memberOrganizationTeamRepository
                .findMemberOrganizationTeam(member.getId(), organization, null);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMember().getId()).isEqualTo(member.getId());
        assertThat(result.get().getOrganization().getId()).isEqualTo(organization.getId());
    }

    @Test
    @DisplayName("organizationName으로 MemberOrganizationTeam을 조회한다")
    void findMemberOrganizationTeam_byOrganizationName() {
        // given
        saveMot(member, organization, "내과팀");

        // when
        Optional<MemberOrganizationTeam> result = memberOrganizationTeamRepository
                .findMemberOrganizationTeam(null, null, "병원A");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getOrganization().getOrganizationName()).isEqualTo("병원A");
    }

    @Test
    @DisplayName("모든 조건이 null이면 데이터가 없을 때 empty를 반환한다")
    void findMemberOrganizationTeam_allNull_empty() {
        // when (데이터 없음)
        Optional<MemberOrganizationTeam> result = memberOrganizationTeamRepository
                .findMemberOrganizationTeam(null, null, null);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 memberId로 조회하면 empty를 반환한다")
    void findMemberOrganizationTeam_wrongMemberId() {
        // given
        saveMot(member, organization, "내과팀");

        // when
        Optional<MemberOrganizationTeam> result = memberOrganizationTeamRepository
                .findMemberOrganizationTeam(9999L, organization, null);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다른 organization으로 조회하면 empty를 반환한다")
    void findMemberOrganizationTeam_wrongOrganization() {
        // given
        saveMot(member, organization, "내과팀");
        Organization otherOrg = organizationRepository.save(Organization.builder()
                .organizationName("병원B")
                .team("외과팀")
                .organizationMember(otherMember)
                .build());

        // when
        Optional<MemberOrganizationTeam> result = memberOrganizationTeamRepository
                .findMemberOrganizationTeam(member.getId(), otherOrg, null);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("memberId, organization, organizationName 모두 조건으로 조회한다")
    void findMemberOrganizationTeam_allConditions() {
        // given
        saveMot(member, organization, "내과팀");

        // when
        Optional<MemberOrganizationTeam> result = memberOrganizationTeamRepository
                .findMemberOrganizationTeam(member.getId(), organization, "병원A");

        // then
        assertThat(result).isPresent();
    }

    // ===== 기존 코드와 동작 일치 검증 =====

    @Test
    @DisplayName("memberId + organizationName으로 조회 성공 (findByMemberAndOrganizationName 대체)")
    void findMemberOrganizationTeam_byMemberIdAndOrganizationName_replacesJpaMethod() {
        // given
        saveMot(member, organization, "내과팀");

        // when
        Optional<MemberOrganizationTeam> result = memberOrganizationTeamRepository
                .findMemberOrganizationTeam(member.getId(), null, "병원A");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMember().getId()).isEqualTo(member.getId());
        assertThat(result.get().getOrganization().getOrganizationName()).isEqualTo("병원A");
    }

    @Test
    @DisplayName("존재하지 않는 memberId로 조회하면 empty (findByMemberAndOrganizationName 대체)")
    void findMemberOrganizationTeam_wrongMemberId_byOrganizationName() {
        // given
        saveMot(member, organization, "내과팀");

        // when
        Optional<MemberOrganizationTeam> result = memberOrganizationTeamRepository
                .findMemberOrganizationTeam(9999L, null, "병원A");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 organizationName으로 조회하면 empty (findByMemberAndOrganizationName 대체)")
    void findMemberOrganizationTeam_wrongOrganizationName_byOrganizationName() {
        // given
        saveMot(member, organization, "내과팀");

        // when
        Optional<MemberOrganizationTeam> result = memberOrganizationTeamRepository
                .findMemberOrganizationTeam(member.getId(), null, "없는병원");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다른 멤버의 데이터는 조회되지 않는다 (findByMemberAndOrganizationName 대체)")
    void findMemberOrganizationTeam_otherMember_byOrganizationName() {
        // given
        saveMot(otherMember, organization, "내과팀");

        // when
        Optional<MemberOrganizationTeam> result = memberOrganizationTeamRepository
                .findMemberOrganizationTeam(member.getId(), null, "병원A");

        // then
        assertThat(result).isEmpty();
    }
}