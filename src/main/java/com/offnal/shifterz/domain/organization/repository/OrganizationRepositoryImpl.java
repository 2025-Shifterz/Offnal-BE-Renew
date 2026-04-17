package com.offnal.shifterz.domain.organization.repository;

import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.organization.domain.QOrganization;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class OrganizationRepositoryImpl implements OrganizationRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Organization> findOrganizations(
            Long memberId,
            String organizationName,
            String team
    ) {
        QOrganization org = QOrganization.organization;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(org.organizationMember.id.eq(memberId));

        if (organizationName != null) {
            builder.and(org.organizationName.eq(organizationName));
        }
        if (team != null) {
            builder.and(org.team.eq(team));
        }

        return queryFactory
                .selectFrom(org)
                .where(builder)
                .orderBy(org.id.asc())
                .fetch();
    }

    @Override
    public Optional<Organization> findOrganization(
            Long memberId,
            String organizationName,
            String team
    ) {
        QOrganization org = QOrganization.organization;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(org)
                        .where(
                                org.organizationMember.id.eq(memberId),
                                org.organizationName.eq(organizationName),
                                org.team.eq(team)
                        )
                        .fetchOne()
        );
    }

    @Override
    public boolean existsOrganization(
            Long memberId,
            String organizationName,
            String team
    ) {
        QOrganization org = QOrganization.organization;

        return queryFactory
                .selectOne()
                .from(org)
                .where(
                        org.organizationMember.id.eq(memberId),
                        org.organizationName.eq(organizationName),
                        org.team.eq(team)
                )
                .fetchFirst() != null;
    }
}
