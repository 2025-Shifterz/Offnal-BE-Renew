package com.offnal.shifterz.domain.memo.repository;

import com.offnal.shifterz.core.config.QueryDslConfig;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.domain.memo.domain.Memo;
import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.organization.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemoRepositoryTest {
    @Autowired
    private MemoRepository memoRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Member member;
    private Member otherMember;
    private Organization organization;
    private final LocalDate targetDate = LocalDate.of(2025, 6, 15);

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

    private Memo saveMemo(Member m, Organization org, LocalDate date) {
        return memoRepository.save(Memo.builder()
                .title("메모 제목")
                .content("메모 내용")
                .targetDate(date)
                .member(m)
                .organization(org)
                .build());
    }

    @Test
    @DisplayName("특정 날짜의 조직 메모를 조회한다")
    void findMemosWithFilters_byOrganization() {
        // given
        saveMemo(member, organization, targetDate);
        saveMemo(member, null, targetDate); // unassigned

        // when
        List<Memo> result = memoRepository.findMemosWithFilters(member, organization.getId(), false, targetDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrganization().getId()).isEqualTo(organization.getId());
    }

    @Test
    @DisplayName("unassigned=true이면 조직이 null인 메모만 조회한다")
    void findMemosWithFilters_unassigned() {
        // given
        saveMemo(member, null, targetDate);
        saveMemo(member, null, targetDate);
        saveMemo(member, organization, targetDate);

        // when
        List<Memo> result = memoRepository.findMemosWithFilters(member, null, true, targetDate);

        // then
        assertThat(result).hasSize(2)
                .allMatch(m -> m.getOrganization() == null);
    }

    @Test
    @DisplayName("날짜가 다른 메모는 조회되지 않는다")
    void findMemosWithFilters_differentDate() {
        // given
        saveMemo(member, organization, targetDate);
        saveMemo(member, organization, targetDate.plusDays(1));

        // when
        List<Memo> result = memoRepository.findMemosWithFilters(member, organization.getId(), false, targetDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTargetDate()).isEqualTo(targetDate);
    }

    @Test
    @DisplayName("다른 멤버의 메모는 조회되지 않는다")
    void findMemosWithFilters_otherMember() {
        // given
        saveMemo(otherMember, organization, targetDate);

        // when
        List<Memo> result = memoRepository.findMemosWithFilters(member, organization.getId(), false, targetDate);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("organizationId가 null이고 unassigned=false이면 조직 필터를 적용하지 않는다")
    void findMemosWithFilters_noOrganizationFilter() {
        // given
        saveMemo(member, organization, targetDate);
        saveMemo(member, null, targetDate);

        // when
        List<Memo> result = memoRepository.findMemosWithFilters(member, null, false, targetDate);

        // then
        // organizationId=null, unassigned=false → 조직 조건 없이 해당 날짜 전체 조회
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("결과가 없으면 빈 리스트를 반환한다")
    void findMemosWithFilters_empty() {
        // when
        List<Memo> result = memoRepository.findMemosWithFilters(member, null, false, targetDate);

        // then
        assertThat(result).isEmpty();
    }

    // ===== 기존 코드와 동작 일치 검증 =====

    @Test
    @DisplayName("[동작 변경] unassigned=false, organizationId 있음 → 새 코드는 조직 필터 적용, 기존 코드는 필터 없음")
    void findMemosWithFilters_behaviorChange_organizationFilter() {
        // given
        saveMemo(member, organization, targetDate); // 조직 있는 메모
        saveMemo(member, null, targetDate); // 조직 없는 메모

        // when
        List<Memo> result = memoRepository.findMemosWithFilters(
                member, organization.getId(), false, targetDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrganization().getId()).isEqualTo(organization.getId());
    }

    @Test
    @DisplayName("unassigned=false, organizationId=null → 조직 필터 없이 전체 조회 (기존과 동일)")
    void findMemosWithFilters_noFilter_sameAsBefore() {
        // given
        saveMemo(member, organization, targetDate);
        saveMemo(member, null, targetDate);

        // when
        List<Memo> result = memoRepository.findMemosWithFilters(
                member, null, false, targetDate);

        // then — 기존/신규 모두 조직 조건 없이 2건 반환
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("unassigned=true → 조직 없는 메모만 조회")
    void findMemosWithFilters_unassigned_sameAsBefore() {
        // given
        saveMemo(member, null, targetDate);
        saveMemo(member, null, targetDate);
        saveMemo(member, organization, targetDate);

        // when
        List<Memo> result = memoRepository.findMemosWithFilters(
                member, null, true, targetDate);

        // then
        assertThat(result).hasSize(2)
                .allMatch(m -> m.getOrganization() == null);
    }

    @Test
    @DisplayName("unassigned=true + organizationId 있으면 조건 충돌로 빈 결과를 반환한다")
    void findMemosWithFilters_unassigned_withOrganizationId_returnsEmpty() {
        // given
        saveMemo(member, null, targetDate); // organization=null
        saveMemo(member, organization, targetDate); // organization 존재

        // when — unassigned=true(organization IS NULL) AND organization.id=?
        List<Memo> result = memoRepository.findMemosWithFilters(
                member, organization.getId(), true, targetDate);

        // then — 두 조건이 충돌하므로 항상 empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("날짜 경계값 — targetDate 당일만 조회되고 전날/다음날은 제외")
    void findMemosWithFilters_dateBoundary_sameAsBefore() {
        // given
        saveMemo(member, null, targetDate.minusDays(1));
        saveMemo(member, null, targetDate);
        saveMemo(member, null, targetDate.plusDays(1));

        // when
        List<Memo> result = memoRepository.findMemosWithFilters(
                member, null, false, targetDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTargetDate()).isEqualTo(targetDate);
    }

    @Test
    @DisplayName("다른 멤버의 메모는 조회되지 않는다")
    void findMemosWithFilters_memberIsolation_sameAsBefore() {
        // given
        saveMemo(otherMember, null, targetDate);
        saveMemo(member, null, targetDate);

        // when
        List<Memo> result = memoRepository.findMemosWithFilters(
                member, null, false, targetDate);

        // then
        assertThat(result).hasSize(1)
                .allMatch(m -> m.getMember().getId().equals(member.getId()));
    }
}