package com.offnal.shifterz.domain.work.repository;

import com.offnal.shifterz.core.config.QueryDslConfig;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.organization.repository.OrganizationRepository;
import com.offnal.shifterz.domain.work.domain.WorkCalendar;
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
class WorkCalendarRepositoryTest {
    @Autowired
    private WorkCalendarRepository workCalendarRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Member member;
    private Organization organization;
    private Organization otherOrg;
    private Long memberId;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
                .provider(Provider.KAKAO)
                .providerId("12345678")
                .build());
        memberId = member.getId();

        organization = organizationRepository.save(Organization.builder()
                .organizationName("병원A")
                .team("내과팀")
                .organizationMember(member)
                .build());

        otherOrg = organizationRepository.save(Organization.builder()
                .organizationName("병원B")
                .team("외과팀")
                .organizationMember(member)
                .build());
    }

    private WorkCalendar saveCalendar(Long mId, Organization org) {
        return workCalendarRepository.save(WorkCalendar.builder()
                .memberId(mId)
                .organization(org)
                .build());
    }

    // ===== existsByMemberIdAndOrganization =====

    @Test
    @DisplayName("memberId와 organization에 해당하는 캘린더가 존재하면 true를 반환한다")
    void existsByMemberIdAndOrganization_true() {
        // given
        saveCalendar(memberId, organization);

        // when
        boolean result = workCalendarRepository.existsByMemberIdAndOrganization(memberId, organization);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("캘린더가 없으면 false를 반환한다")
    void existsByMemberIdAndOrganization_false() {
        // when
        boolean result = workCalendarRepository.existsByMemberIdAndOrganization(memberId, organization);

        // then
        assertThat(result).isFalse();
    }

    // ===== findByMemberIdAndOrganizationOrderByIdDesc =====

    @Test
    @DisplayName("캘린더 목록을 id 내림차순으로 조회한다")
    void findByMemberIdAndOrganizationOrderByIdDesc() {
        // given
        WorkCalendar cal1 = saveCalendar(memberId, organization);
        WorkCalendar cal2 = saveCalendar(memberId, organization);
        WorkCalendar cal3 = saveCalendar(memberId, organization);

        // when
        List<WorkCalendar> result = workCalendarRepository
                .findByMemberIdAndOrganizationOrderByIdDesc(memberId, organization);

        // then
        assertThat(result).hasSize(3)
                .extracting(WorkCalendar::getId)
                .containsExactly(cal3.getId(), cal2.getId(), cal1.getId());
    }

    @Test
    @DisplayName("다른 organization의 캘린더는 조회되지 않는다")
    void findByMemberIdAndOrganizationOrderByIdDesc_excludeOtherOrg() {
        // given
        saveCalendar(memberId, organization);
        saveCalendar(memberId, otherOrg);

        // when
        List<WorkCalendar> result = workCalendarRepository
                .findByMemberIdAndOrganizationOrderByIdDesc(memberId, organization);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrganization().getId()).isEqualTo(organization.getId());
    }

    // ===== findCalendar =====

    @Test
    @DisplayName("calendarId 있을 때: id, memberId, organization이 모두 일치하는 캘린더를 조회한다")
    void findCalendar_withCalendarId_success() {
        // given
        WorkCalendar calendar = saveCalendar(memberId, organization);

        // when
        Optional<WorkCalendar> result = workCalendarRepository
                .findCalendar(calendar.getId(), memberId, organization);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(calendar.getId());
    }

    @Test
    @DisplayName("calendarId null일 때: memberId, organization으로 캘린더를 조회한다")
    void findCalendar_withNullCalendarId_success() {
        // given
        WorkCalendar calendar = saveCalendar(memberId, organization);

        // when
        Optional<WorkCalendar> result = workCalendarRepository
                .findCalendar(null, memberId, organization);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(calendar.getId());
    }

    @Test
    @DisplayName("calendarId 있을 때: organization이 다르면 empty를 반환한다")
    void findCalendar_withCalendarId_wrongOrg_returnsEmpty() {
        // given
        WorkCalendar calendar = saveCalendar(memberId, organization);

        // when
        Optional<WorkCalendar> result = workCalendarRepository
                .findCalendar(calendar.getId(), memberId, otherOrg);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("calendarId null일 때: organization이 다르면 empty를 반환한다")
    void findCalendar_withNullCalendarId_wrongOrg_returnsEmpty() {
        // given
        saveCalendar(memberId, organization);

        // when
        Optional<WorkCalendar> result = workCalendarRepository
                .findCalendar(null, memberId, otherOrg);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("calendarId 있을 때: 존재하지 않는 id면 empty를 반환한다")
    void findCalendar_withCalendarId_wrongId_returnsEmpty() {
        // given
        saveCalendar(memberId, organization);

        // when
        Optional<WorkCalendar> result = workCalendarRepository
                .findCalendar(9999L, memberId, organization);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("calendarId null일 때: 캘린더가 없으면 empty를 반환한다")
    void findCalendar_withNullCalendarId_noData_returnsEmpty() {
        // when
        Optional<WorkCalendar> result = workCalendarRepository
                .findCalendar(null, memberId, organization);

        // then
        assertThat(result).isEmpty();
    }
}