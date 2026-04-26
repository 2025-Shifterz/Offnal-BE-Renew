package com.offnal.shifterz.domain.memo.repository;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.memo.domain.Memo;

import java.time.LocalDate;
import java.util.List;

public interface MemoRepositoryCustom {
    List<Memo> findMemosWithFilters(
            Member member, Long organizationId,boolean unassigned, LocalDate targetDate
    );
}
