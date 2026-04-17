package com.offnal.shifterz.domain.memberOrganizationTeam.repository;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.domain.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberOrganizationTeamRepository extends JpaRepository<MemberOrganizationTeam, Long> {
    Optional<MemberOrganizationTeam> findByMemberId(Long memberId);

    Optional<MemberOrganizationTeam> findByMemberAndOrganization(Member member, Organization organization);

    Optional<MemberOrganizationTeam> findByMemberIdAndOrganizationOrganizationName(Long memberId, String organizationName);

    void deleteAllByMemberId(Long memberId);
    void deleteAllByOrganization(Organization organization);
}
