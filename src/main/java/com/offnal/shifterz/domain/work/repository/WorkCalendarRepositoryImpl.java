package com.offnal.shifterz.domain.work.repository;

import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.work.domain.WorkCalendar;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.offnal.shifterz.domain.work.domain.QWorkCalendar.workCalendar;

@Repository
@RequiredArgsConstructor
public class WorkCalendarRepositoryImpl implements WorkCalendarRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<WorkCalendar> findCalendar(Long calendarId, Long memberId, Organization organization) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(workCalendar)
                        .where(
                                calendarIdEq(calendarId),
                                workCalendar.memberId.eq(memberId),
                                workCalendar.organization.eq(organization)
                        )
                        .fetchOne()
        );
    }

    private BooleanExpression calendarIdEq(Long calendarId) {
        return calendarId != null ? workCalendar.id.eq(calendarId) : null;
    }
}
