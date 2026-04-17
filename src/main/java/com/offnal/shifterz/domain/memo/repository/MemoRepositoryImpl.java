package com.offnal.shifterz.domain.memo.repository;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.memo.domain.Memo;
import com.offnal.shifterz.domain.memo.domain.QMemo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class MemoRepositoryImpl implements MemoRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Memo> findMemosWithFilters(Member member, Long organizationId,
                                           boolean unassigned, LocalDate targetDate) {
        QMemo memo = QMemo.memo;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(memo.member.eq(member));
        builder.and(memo.targetDate.year().eq(targetDate.getYear())
                .and(memo.targetDate.month().eq(targetDate.getMonthValue()))
                .and(memo.targetDate.dayOfMonth().eq(targetDate.getDayOfMonth())));

        if (unassigned) {
            builder.and(memo.organization.isNull());
        } else if (organizationId != null) {
            builder.and(memo.organization.id.eq(organizationId));
        }

        return queryFactory.selectFrom(memo).where(builder).fetch();
    }
}
