package com.offnal.shifterz.domain.work.repository;

import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.work.domain.QWorkCalendar;
import com.offnal.shifterz.domain.work.domain.QWorkInstance;
import com.offnal.shifterz.domain.work.domain.WorkInstance;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class WorkInstanceRepositoryImpl implements WorkInstanceRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<WorkInstance> findByOrganizationIdAndDateRange(
            Long organizationId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        QWorkInstance wi = QWorkInstance.workInstance;
        QWorkCalendar wc = QWorkCalendar.workCalendar;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(wc.organization.id.eq(organizationId));

        if (startDate != null) builder.and(wi.workDate.goe(startDate));
        if (endDate != null)   builder.and(wi.workDate.loe(endDate));

        return queryFactory
                .selectFrom(wi)
                .join(wi.workCalendar, wc).fetchJoin()
                .join(wc.organization).fetchJoin()
                .where(builder)
                .orderBy(wi.workDate.asc())
                .fetch();
    }

    @Override
    public Optional<WorkInstance> findWorkInstance(
            Long memberId,
            LocalDate workDate,
            Organization organization
    ) {
        QWorkInstance wi = QWorkInstance.workInstance;
        QWorkCalendar wc = QWorkCalendar.workCalendar;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(wc.memberId.eq(memberId));
        builder.and(wi.workDate.eq(workDate));

        if (organization != null) {
            builder.and(wc.organization.eq(organization));
        }

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(wi)
                        .join(wi.workCalendar, wc)
                        .where(builder)
                        .fetchOne()
        );
    }

    @Override
    public List<WorkInstance> findWorkInstances(
            Long memberId,
            Organization organization,
            LocalDate startDate,
            LocalDate endDate
    ) {
        QWorkInstance wi = QWorkInstance.workInstance;
        QWorkCalendar wc = QWorkCalendar.workCalendar;

        return queryFactory
                .selectFrom(wi)
                .join(wi.workCalendar, wc)
                .where(
                        wc.memberId.eq(memberId),
                        wc.organization.eq(organization),
                        wi.workDate.between(startDate, endDate)
                )
                .orderBy(wi.workDate.asc())
                .fetch();
    }
}
