package com.offnal.shifterz.domain.organization.repository;

import com.offnal.shifterz.core.config.QueryDslConfig;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.domain.organization.domain.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrganizationRepositoryTest {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Member otherMember;

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
    }

    private Organization saveOrganization(Member owner, String orgName, String team) {
        return organizationRepository.save(
                Organization.builder()
                        .organizationName(orgName)
                        .team(team)
                        .organizationMember(owner)
                        .build()
        );
    }

    // ===== findOrganizations =====

    @Test
    @DisplayName("memberId만으로 해당 멤버의 모든 조직을 조회한다")
    void findOrganizations_byMemberIdOnly() {
        // given
        saveOrganization(member, "병원A", "내과팀");
        saveOrganization(member, "병원B", "외과팀");
        saveOrganization(otherMember, "병원C", "응급팀");

        // when
        List<Organization> result = organizationRepository.findOrganizations(member.getId(), null, null);

        // then
        assertThat(result).hasSize(2)
                .extracting(Organization::name)
                .containsExactlyInAnyOrder("병원A", "병원B");
    }

    @Test
    @DisplayName("organizationName 필터로 조회한다")
    void findOrganizations_byOrganizationName() {
        // given
        saveOrganization(member, "병원A", "내과팀");
        saveOrganization(member, "병원A", "외과팀");
        saveOrganization(member, "병원B", "내과팀");

        // when
        List<Organization> result = organizationRepository.findOrganizations(member.getId(), "병원A", null);

        // then
        assertThat(result).hasSize(2)
                .allMatch(o -> o.name().equals("병원A"));
    }

    @Test
    @DisplayName("team 필터로 조회한다")
    void findOrganizations_byTeam() {
        // given
        saveOrganization(member, "병원A", "내과팀");
        saveOrganization(member, "병원B", "내과팀");
        saveOrganization(member, "병원C", "외과팀");

        // when
        List<Organization> result = organizationRepository.findOrganizations(member.getId(), null, "내과팀");

        // then
        assertThat(result).hasSize(2)
                .allMatch(o -> o.team().equals("내과팀"));
    }

    @Test
    @DisplayName("organizationName과 team 모두 필터로 조회한다")
    void findOrganizations_byNameAndTeam() {
        // given
        saveOrganization(member, "병원A", "내과팀");
        saveOrganization(member, "병원A", "외과팀");
        saveOrganization(member, "병원B", "내과팀");

        // when
        List<Organization> result = organizationRepository.findOrganizations(member.getId(), "병원A", "내과팀");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("병원A");
        assertThat(result.get(0).team()).isEqualTo("내과팀");
    }

    @Test
    @DisplayName("결과가 없으면 빈 리스트를 반환한다")
    void findOrganizations_empty() {
        // when
        List<Organization> result = organizationRepository.findOrganizations(member.getId(), "없는병원", null);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("조회 결과는 id 오름차순으로 정렬된다")
    void findOrganizations_orderedById() {
        // given
        Organization org1 = saveOrganization(member, "병원A", "팀1");
        Organization org2 = saveOrganization(member, "병원B", "팀2");
        Organization org3 = saveOrganization(member, "병원C", "팀3");

        // when
        List<Organization> result = organizationRepository.findOrganizations(member.getId(), null, null);

        // then
        assertThat(result).extracting(Organization::getId)
                .containsExactly(org1.getId(), org2.getId(), org3.getId());
    }

    // ===== findOrganization =====

    @Test
    @DisplayName("memberId, organizationName, team 모두 일치하는 조직을 조회한다")
    void findOrganization_success() {
        // given
        saveOrganization(member, "병원A", "내과팀");

        // when
        Optional<Organization> result = organizationRepository.findOrganization(member.getId(), "병원A", "내과팀");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("병원A");
    }

    @Test
    @DisplayName("팀이 다르면 조회되지 않는다")
    void findOrganization_wrongTeam() {
        // given
        saveOrganization(member, "병원A", "내과팀");

        // when
        Optional<Organization> result = organizationRepository.findOrganization(member.getId(), "병원A", "외과팀");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다른 멤버의 조직은 조회되지 않는다")
    void findOrganization_otherMember() {
        // given
        saveOrganization(otherMember, "병원A", "내과팀");

        // when
        Optional<Organization> result = organizationRepository.findOrganization(member.getId(), "병원A", "내과팀");

        // then
        assertThat(result).isEmpty();
    }

    // ===== existsOrganization =====

    @Test
    @DisplayName("조건에 맞는 조직이 존재하면 true를 반환한다")
    void existsOrganization_true() {
        // given
        saveOrganization(member, "병원A", "내과팀");

        // when
        boolean result = organizationRepository.existsOrganization(member.getId(), "병원A", "내과팀");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("조건에 맞는 조직이 없으면 false를 반환한다")
    void existsOrganization_false() {
        // when
        boolean result = organizationRepository.existsOrganization(member.getId(), "없는병원", "없는팀");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 멤버의 조직은 존재하지 않는 것으로 판단한다")
    void existsOrganization_otherMember() {
        // given
        saveOrganization(otherMember, "병원A", "내과팀");

        // when
        boolean result = organizationRepository.existsOrganization(member.getId(), "병원A", "내과팀");

        // then
        assertThat(result).isFalse();
    }

    // ===== 기존 코드와 동작 일치 검증 =====

    @Test
    @DisplayName("memberId만으로 전체 조회 — id 오름차순 정렬 (findAllByOrganizationMember_IdOrderByIdAsc 대체)")
    void findOrganizations_memberId_replacesJpaMethod() {
        // given
        Organization org1 = saveOrganization(member, "병원A", "내과팀");
        Organization org2 = saveOrganization(member, "병원B", "외과팀");
        saveOrganization(otherMember, "병원C", "응급팀");

        // when
        List<Organization> result = organizationRepository
                .findOrganizations(member.getId(), null, null);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Organization::getId)
                .containsExactly(org1.getId(), org2.getId()); // id 오름차순
    }

    @Test
    @DisplayName("name 필터 + id 오름차순 (findAllByOrganizationMember_IdAndOrganizationNameOrderByIdAsc 대체)")
    void findOrganizations_memberIdAndName_replacesJpaMethod() {
        // given
        Organization org1 = saveOrganization(member, "병원A", "내과팀");
        Organization org2 = saveOrganization(member, "병원A", "외과팀");
        saveOrganization(member, "병원B", "내과팀");

        // when
        List<Organization> result = organizationRepository
                .findOrganizations(member.getId(), "병원A", null);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Organization::getId)
                .containsExactly(org1.getId(), org2.getId()); // id 오름차순
    }

    @Test
    @DisplayName("단건 조회 성공 (findByOrganizationMember_IdAndOrganizationNameAndTeam 대체)")
    void findOrganization_replacesJpaMethod() {
        // given
        Organization saved = saveOrganization(member, "병원A", "내과팀");

        // when
        Optional<Organization> result = organizationRepository
                .findOrganization(member.getId(), "병원A", "내과팀");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("단건 조회 — 조건 불일치 시 empty (findByOrganizationMember_IdAndOrganizationNameAndTeam 대체)")
    void findOrganization_notFound_replacesJpaMethod() {
        // given
        saveOrganization(member, "병원A", "내과팀");

        // when
        Optional<Organization> result = organizationRepository
                .findOrganization(member.getId(), "병원A", "외과팀");

        // then
        assertThat(result).isEmpty();
    }
}