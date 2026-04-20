package com.offnal.shifterz.domain.todo.repository;

import com.offnal.shifterz.core.config.QueryDslConfig;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.domain.Provider;
import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.organization.repository.OrganizationRepository;
import com.offnal.shifterz.domain.todo.domain.Todo;
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
class TodoRepositoryTest {
    @Autowired
    private TodoRepository todoRepository;

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

    private Todo saveTodo(Member m, Organization org, LocalDate date) {
        return todoRepository.save(Todo.builder()
                .content("할 일 내용")
                .completed(false)
                .targetDate(date)
                .member(m)
                .organization(org)
                .build());
    }

    @Test
    @DisplayName("특정 날짜의 조직 Todo를 조회한다")
    void findTodosWithFilters_byOrganization() {
        // given
        saveTodo(member, organization, targetDate);
        saveTodo(member, null, targetDate); // unassigned

        // when
        List<Todo> result = todoRepository.findTodosWithFilters(member, organization.getId(), false, targetDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrganization().getId()).isEqualTo(organization.getId());
    }

    @Test
    @DisplayName("unassigned=true이면 조직이 null인 Todo만 조회한다")
    void findTodosWithFilters_unassigned() {
        // given
        saveTodo(member, null, targetDate);
        saveTodo(member, null, targetDate);
        saveTodo(member, organization, targetDate);

        // when
        List<Todo> result = todoRepository.findTodosWithFilters(member, null, true, targetDate);

        // then
        assertThat(result).hasSize(2)
                .allMatch(t -> t.getOrganization() == null);
    }

    @Test
    @DisplayName("날짜가 다른 Todo는 조회되지 않는다")
    void findTodosWithFilters_differentDate() {
        // given
        saveTodo(member, organization, targetDate);
        saveTodo(member, organization, targetDate.minusDays(1));

        // when
        List<Todo> result = todoRepository.findTodosWithFilters(member, organization.getId(), false, targetDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTargetDate()).isEqualTo(targetDate);
    }

    @Test
    @DisplayName("다른 멤버의 Todo는 조회되지 않는다")
    void findTodosWithFilters_otherMember() {
        // given
        saveTodo(otherMember, organization, targetDate);

        // when
        List<Todo> result = todoRepository.findTodosWithFilters(member, organization.getId(), false, targetDate);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("organizationId가 null이고 unassigned=false이면 조직 필터를 적용하지 않는다")
    void findTodosWithFilters_noOrganizationFilter() {
        // given
        saveTodo(member, organization, targetDate);
        saveTodo(member, null, targetDate);

        // when
        List<Todo> result = todoRepository.findTodosWithFilters(member, null, false, targetDate);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("결과가 없으면 빈 리스트를 반환한다")
    void findTodosWithFilters_empty() {
        // when
        List<Todo> result = todoRepository.findTodosWithFilters(member, null, false, targetDate);

        // then
        assertThat(result).isEmpty();
    }

    // ===== 기존 코드와 동작 일치 검증 =====

    @Test
    @DisplayName("[동작 변경] unassigned=false, organizationId 있음 → 새 코드는 조직 필터 적용, 기존 코드는 필터 없음")
    void findTodosWithFilters_behaviorChange_organizationFilter() {
        // given
        saveTodo(member, organization, targetDate);
        saveTodo(member, null, targetDate);

        // when
        List<Todo> result = todoRepository.findTodosWithFilters(
                member, organization.getId(), false, targetDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrganization().getId()).isEqualTo(organization.getId());
    }

    @Test
    @DisplayName("unassigned=true + organizationId 있으면 조건 충돌로 빈 결과를 반환한다")
    void findTodosWithFilters_unassigned_withOrganizationId_returnsEmpty() {
        // given
        saveTodo(member, null, targetDate);
        saveTodo(member, organization, targetDate);

        // when — unassigned=true(organization IS NULL) AND organization.id=?
        List<Todo> result = todoRepository.findTodosWithFilters(
                member, organization.getId(), true, targetDate);

        // then — 두 조건이 충돌하므로 항상 empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("unassigned=false, organizationId=null → 조직 필터 없이 전체 조회 (기존과 동일)")
    void findTodosWithFilters_noFilter_sameAsBefore() {
        // given
        saveTodo(member, organization, targetDate);
        saveTodo(member, null, targetDate);

        // when
        List<Todo> result = todoRepository.findTodosWithFilters(
                member, null, false, targetDate);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("날짜 경계값 — targetDate 당일만 조회되고 전날/다음날은 제외 (기존과 동일)")
    void findTodosWithFilters_dateBoundary_sameAsBefore() {
        // given
        saveTodo(member, null, targetDate.minusDays(1));
        saveTodo(member, null, targetDate);
        saveTodo(member, null, targetDate.plusDays(1));

        // when
        List<Todo> result = todoRepository.findTodosWithFilters(
                member, null, false, targetDate);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTargetDate()).isEqualTo(targetDate);
    }

    @Test
    @DisplayName("다른 멤버의 Todo는 조회되지 않는다 (기존과 동일)")
    void findTodosWithFilters_memberIsolation_sameAsBefore() {
        // given
        saveTodo(otherMember, null, targetDate);
        saveTodo(member, null, targetDate);

        // when
        List<Todo> result = todoRepository.findTodosWithFilters(
                member, null, false, targetDate);

        // then
        assertThat(result).hasSize(1)
                .allMatch(t -> t.getMember().getId().equals(member.getId()));
    }
}