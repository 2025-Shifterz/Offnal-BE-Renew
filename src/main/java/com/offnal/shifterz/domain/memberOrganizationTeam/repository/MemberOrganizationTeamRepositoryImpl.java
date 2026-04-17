package com.offnal.shifterz.domain.memberOrganizationTeam.repository;

import com.offnal.shifterz.domain.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.domain.memberOrganizationTeam.domain.QMemberOrganizationTeam;
import com.offnal.shifterz.domain.organization.domain.Organization;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberOrganizationTeamRepositoryImpl implements MemberOrganizationTeamRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberOrganizationTeam> findMemberOrganizationTeam(
            Long memberId,
            Organization organization,
            String organizationName
    ) {
        QMemberOrganizationTeam mot = QMemberOrganizationTeam.memberOrganizationTeam;
        BooleanBuilder builder = new BooleanBuilder();

        if (memberId != null) {
            builder.and(mot.member.id.eq(memberId));
        }
        if (organization != null) {
            builder.and(mot.organization.eq(organization));
        }
        if (organizationName != null) {
            builder.and(mot.organization.organizationName.eq(organizationName));
        }

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(mot)
                        .where(builder)
                        .fetchOne()
        );
    }
}
