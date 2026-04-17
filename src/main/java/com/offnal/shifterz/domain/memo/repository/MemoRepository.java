package com.offnal.shifterz.domain.memo.repository;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.memo.domain.Memo;
import com.offnal.shifterz.domain.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    @Query("""
    SELECT m
    FROM Memo m
    WHERE m.member = :member
      AND (:organizationId IS NULL OR m.organization.id = :organizationId)
      AND (:unassigned = TRUE AND m.organization IS NULL OR :unassigned = FALSE)
      AND FUNCTION('DATE', m.targetDate) = :targetDate
""")
    List<Memo> findMemosWithFilters(
            @Param("member") Member member,
            @Param("organizationId") Long organizationId,
            @Param("unassigned") boolean unassigned,
            @Param("targetDate") LocalDate targetDate
    );

    void deleteByMemberId(Long memberId);

    void deleteAllByOrganization(Organization organization);
}
