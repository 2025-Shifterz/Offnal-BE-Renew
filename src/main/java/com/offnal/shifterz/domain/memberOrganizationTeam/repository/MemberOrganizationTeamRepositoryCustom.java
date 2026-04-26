package com.offnal.shifterz.domain.memberOrganizationTeam.repository;

import com.offnal.shifterz.domain.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.domain.organization.domain.Organization;

import java.util.Optional;

public interface MemberOrganizationTeamRepositoryCustom {
    Optional<MemberOrganizationTeam> findMemberOrganizationTeam(
            Long memberId,
            Organization organization,
            String organizationName
    );
}
