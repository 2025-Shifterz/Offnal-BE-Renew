package com.offnal.shifterz.domain.todo.repository;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("""
    SELECT t
    FROM Todo t
    WHERE t.member = :member
      AND (:organizationId IS NULL OR t.organization.id = :organizationId)
      AND (:unassigned = TRUE AND t.organization IS NULL OR :unassigned = FALSE)
      AND FUNCTION('DATE', t.targetDate) = :targetDate
""")
    List<Todo> findTodosWithFilters(
            @Param("member") Member member,
            @Param("organizationId") Long organizationId,
            @Param("unassigned") boolean unassigned,
            @Param("targetDate") LocalDate targetDate
    );
    void deleteAllByOrganization(Organization organization);

    void deleteByMemberId(Long memberId);
}
