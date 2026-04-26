package com.offnal.shifterz.domain.work.repository;

import com.offnal.shifterz.core.config.QueryDslConfig;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.organization.repository.OrganizationRepository;
import com.offnal.shifterz.domain.work.domain.WorkCalendar;
import com.offnal.shifterz.domain.work.domain.WorkInstance;
import com.offnal.shifterz.domain.work.domain.WorkTimeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WorkInstanceRepositoryTest {
    @Autowired
    private WorkInstanceRepository workInstanceRepository;

    @Autowired
    private WorkCalendarRepository workCalendarRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Member member;
    private Organization organization;
    private WorkCalendar calendar;
    private Long memberId;

    private final LocalDate date1 = LocalDate.of(2025, 6, 1);
    private final LocalDate date2 = LocalDate.of(2025, 6, 15);
    private final LocalDate date3 = LocalDate.of(2025, 6, 30);

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

        calendar = workCalendarRepository.save(WorkCalendar.builder()
                .memberId(memberId)
                .organization(organization)
                .build());
    }

    private WorkInstance saveInstance(WorkCalendar cal, LocalDate date, WorkTimeType type) {
        return workInstanceRepository.save(WorkInstance.builder()
                .workCalendar(cal)
                .workDate(date)
                .workTimeType(type)
                .build());
    }

    // ===== findByOrganizationIdAndDateRange =====

    @Test
    @DisplayName("조직 ID와 날짜 범위로 WorkInstance 목록을 조회한다")
    void findByOrganizationIdAndDateRange_inRange() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);
        saveInstance(calendar, date2, WorkTimeType.NIGHT);
        saveInstance(calendar, date3, WorkTimeType.OFF);

        // when
        List<WorkInstance> result = workInstanceRepository
                .findByOrganizationIdAndDateRange(organization.getId(), date1, date2);

        // then
        assertThat(result).hasSize(2)
                .extracting(WorkInstance::date)
                .containsExactlyInAnyOrder(date1, date2);
    }

    @Test
    @DisplayName("startDate만 지정하면 해당 날짜 이후를 조회한다")
    void findByOrganizationIdAndDateRange_startDateOnly() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);
        saveInstance(calendar, date2, WorkTimeType.NIGHT);
        saveInstance(calendar, date3, WorkTimeType.OFF);

        // when
        List<WorkInstance> result = workInstanceRepository
                .findByOrganizationIdAndDateRange(organization.getId(), date2, null);

        // then
        assertThat(result).hasSize(2)
                .extracting(WorkInstance::date)
                .containsExactlyInAnyOrder(date2, date3);
    }

    @Test
    @DisplayName("endDate만 지정하면 해당 날짜 이전을 조회한다")
    void findByOrganizationIdAndDateRange_endDateOnly() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);
        saveInstance(calendar, date2, WorkTimeType.NIGHT);
        saveInstance(calendar, date3, WorkTimeType.OFF);

        // when
        List<WorkInstance> result = workInstanceRepository
                .findByOrganizationIdAndDateRange(organization.getId(), null, date2);

        // then
        assertThat(result).hasSize(2)
                .extracting(WorkInstance::date)
                .containsExactlyInAnyOrder(date1, date2);
    }

    @Test
    @DisplayName("결과는 날짜 오름차순으로 정렬된다")
    void findByOrganizationIdAndDateRange_orderedByDate() {
        // given
        saveInstance(calendar, date3, WorkTimeType.OFF);
        saveInstance(calendar, date1, WorkTimeType.DAY);
        saveInstance(calendar, date2, WorkTimeType.NIGHT);

        // when
        List<WorkInstance> result = workInstanceRepository
                .findByOrganizationIdAndDateRange(organization.getId(), null, null);

        // then
        assertThat(result).extracting(WorkInstance::date)
                .containsExactly(date1, date2, date3);
    }

    @Test
    @DisplayName("범위 내 데이터가 없으면 빈 리스트를 반환한다")
    void findByOrganizationIdAndDateRange_empty() {
        // when
        List<WorkInstance> result = workInstanceRepository
                .findByOrganizationIdAndDateRange(organization.getId(), date1, date3);

        // then
        assertThat(result).isEmpty();
    }

    // ===== findWorkInstance =====

    @Test
    @DisplayName("memberId, workDate, organization으로 WorkInstance를 조회한다")
    void findWorkInstance_success() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);

        // when
        Optional<WorkInstance> result = workInstanceRepository
                .findWorkInstance(memberId, date1, organization);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().date()).isEqualTo(date1);
    }

    @Test
    @DisplayName("organization이 null이면 organization 조건 없이 조회한다")
    void findWorkInstance_organizationNull() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);

        // when
        Optional<WorkInstance> result = workInstanceRepository
                .findWorkInstance(memberId, date1, null);

        // then
        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("날짜가 다르면 조회되지 않는다")
    void findWorkInstance_wrongDate() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);

        // when
        Optional<WorkInstance> result = workInstanceRepository
                .findWorkInstance(memberId, date2, organization);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("memberId가 다르면 조회되지 않는다")
    void findWorkInstance_wrongMemberId() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);

        // when
        Optional<WorkInstance> result = workInstanceRepository
                .findWorkInstance(9999L, date1, organization);

        // then
        assertThat(result).isEmpty();
    }

    // ===== findWorkInstances =====

    @Test
    @DisplayName("기간 내 WorkInstance 목록을 날짜 오름차순으로 조회한다")
    void findWorkInstances_inRange() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);
        saveInstance(calendar, date2, WorkTimeType.NIGHT);
        saveInstance(calendar, date3, WorkTimeType.OFF);

        // when
        List<WorkInstance> result = workInstanceRepository
                .findWorkInstances(memberId, organization, date1, date2);

        // then
        assertThat(result).hasSize(2)
                .extracting(WorkInstance::date)
                .containsExactly(date1, date2);
    }

    @Test
    @DisplayName("다른 organization의 WorkInstance는 포함되지 않는다")
    void findWorkInstances_excludeOtherOrg() {
        // given
        Organization otherOrg = organizationRepository.save(Organization.builder()
                .organizationName("병원B")
                .team("외과팀")
                .organizationMember(member)
                .build());
        WorkCalendar otherCal = workCalendarRepository.save(WorkCalendar.builder()
                .memberId(memberId)
                .organization(otherOrg)
                .build());

        saveInstance(calendar, date1, WorkTimeType.DAY);
        saveInstance(otherCal, date1, WorkTimeType.NIGHT);

        // when
        List<WorkInstance> result = workInstanceRepository
                .findWorkInstances(memberId, organization, date1, date3);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getWorkCalendar().getOrganization().getId())
                .isEqualTo(organization.getId());
    }

    // ===== findByWorkCalendarOrganizationAndWorkDate (JPQL) =====

    @Test
    @DisplayName("organization과 workDate로 WorkInstance를 조회한다")
    void findByWorkCalendarOrganizationAndWorkDate_success() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);

        // when
        Optional<WorkInstance> result = workInstanceRepository
                .findByWorkCalendarOrganizationAndWorkDate(organization, date1);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().workType()).isEqualTo(WorkTimeType.DAY);
    }

    @Test
    @DisplayName("날짜가 다르면 조회되지 않는다")
    void findByWorkCalendarOrganizationAndWorkDate_wrongDate() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);

        // when
        Optional<WorkInstance> result = workInstanceRepository
                .findByWorkCalendarOrganizationAndWorkDate(organization, date2);

        // then
        assertThat(result).isEmpty();
    }

    // ===== findTypeByDateAndMemberAndOrganization (JPQL) =====

    @Test
    @DisplayName("날짜, memberId, organization으로 WorkTimeType을 조회한다")
    void findTypeByDateAndMemberAndOrganization_success() {
        // given
        saveInstance(calendar, date1, WorkTimeType.NIGHT);

        // when
        Optional<WorkTimeType> result = workInstanceRepository
                .findTypeByDateAndMemberAndOrganization(date1, memberId, organization);

        // then
        assertThat(result).isPresent()
                .hasValue(WorkTimeType.NIGHT);
    }

    @Test
    @DisplayName("데이터가 없으면 empty를 반환한다")
    void findTypeByDateAndMemberAndOrganization_notFound() {
        // when
        Optional<WorkTimeType> result = workInstanceRepository
                .findTypeByDateAndMemberAndOrganization(date1, memberId, organization);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다른 날짜로 조회하면 empty를 반환한다")
    void findTypeByDateAndMemberAndOrganization_wrongDate() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);

        // when
        Optional<WorkTimeType> result = workInstanceRepository
                .findTypeByDateAndMemberAndOrganization(date2, memberId, organization);

        // then
        assertThat(result).isEmpty();
    }

    // ===== 기존 코드와 동작 일치 검증 =====
    @Test
    @DisplayName("memberId + organization + 날짜 범위 조회, 오름차순 정렬")
    void findWorkInstances_replacesJpaMethod() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);
        saveInstance(calendar, date2, WorkTimeType.NIGHT);
        saveInstance(calendar, date3, WorkTimeType.OFF);

        // when
        List<WorkInstance> result = workInstanceRepository
                .findWorkInstances(memberId, organization, date1, date2);

        // then
        assertThat(result).hasSize(2)
                .extracting(WorkInstance::date)
                .containsExactly(date1, date2);
    }

    @Test
    @DisplayName("between 경계값 포함 — startDate 당일만 조회")
    void findWorkInstances_boundaryInclusive_replacesJpaMethod() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);
        saveInstance(calendar, date2, WorkTimeType.NIGHT);

        // when
        List<WorkInstance> result = workInstanceRepository
                .findWorkInstances(memberId, organization, date1, date1);

        // then
        assertThat(result).hasSize(1)
                .extracting(WorkInstance::date)
                .containsExactly(date1);
    }

    @Test
    @DisplayName("다른 organization의 WorkInstance는 제외")
    void findWorkInstances_excludeOtherOrganization_replacesJpaMethod() {
        // given
        Organization otherOrg = organizationRepository.save(Organization.builder()
                .organizationName("병원B")
                .team("외과팀")
                .organizationMember(member)
                .build());
        WorkCalendar otherCalendar = workCalendarRepository.save(WorkCalendar.builder()
                .memberId(memberId)
                .organization(otherOrg)
                .build());

        saveInstance(calendar, date1, WorkTimeType.DAY);
        saveInstance(otherCalendar, date1, WorkTimeType.NIGHT);

        // when
        List<WorkInstance> result = workInstanceRepository
                .findWorkInstances(memberId, organization, date1, date3);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getWorkCalendar().getOrganization().getId())
                .isEqualTo(organization.getId());
    }

    @Test
    @DisplayName("범위 내 데이터 없으면 빈 리스트 (findByWorkCalendarMemberIdAnd...Between 대체)")
    void findWorkInstances_empty_replacesJpaMethod() {
        // when
        List<WorkInstance> result = workInstanceRepository
                .findWorkInstances(memberId, organization, date1, date3);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("memberId + date + organization 단건 조회 ")
    void findWorkInstance_replacesJpaMethod() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);

        // when
        Optional<WorkInstance> result = workInstanceRepository
                .findWorkInstance(memberId, date1, organization);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().date()).isEqualTo(date1);
        assertThat(result.get().workType()).isEqualTo(WorkTimeType.DAY);
    }

    @Test
    @DisplayName("날짜 불일치 시 empty")
    void findWorkInstance_wrongDate_replacesJpaMethod() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);

        // when
        Optional<WorkInstance> result = workInstanceRepository
                .findWorkInstance(memberId, date2, organization);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("memberId 불일치 시 empty")
    void findWorkInstance_wrongMemberId_replacesJpaMethod() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);

        // when
        Optional<WorkInstance> result = workInstanceRepository
                .findWorkInstance(9999L, date1, organization);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("organization 불일치 시 empty")
    void findWorkInstance_wrongOrganization_replacesJpaMethod() {
        // given
        saveInstance(calendar, date1, WorkTimeType.DAY);
        Organization otherOrg = organizationRepository.save(Organization.builder()
                .organizationName("병원B")
                .team("외과팀")
                .organizationMember(member)
                .build());

        // when
        Optional<WorkInstance> result = workInstanceRepository
                .findWorkInstance(memberId, date1, otherOrg);

        // then
        assertThat(result).isEmpty();
    }
}