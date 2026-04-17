package com.offnal.shifterz.domain.memberOrganizationTeam.repository;

import com.offnal.shifterz.domain.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.domain.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberOrganizationTeamRepository extends JpaRepository<MemberOrganizationTeam, Long>, MemberOrganizationTeamRepositoryCustom {
    void deleteAllByMemberId(Long memberId);
    void deleteAllByOrganization(Organization organization);
}
