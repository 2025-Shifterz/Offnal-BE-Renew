package com.offnal.shifterz.domain.organization.repository;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findAllByOrganizationMember_IdOrderByIdAsc(Long memberId);

    Optional<Organization> findByOrganizationMember_IdAndOrganizationNameAndTeam(
            Long memberId, String organizationName, String team
    );

    boolean existsByOrganizationMember_IdAndOrganizationNameAndTeam(
            Long memberId, String organizationName, String team
    );

    void deleteByOrganizationMember_Id(Long memberId);

    List<Organization> findAllByOrganizationMember_IdAndOrganizationNameOrderByIdAsc(
            Long memberId,
            String organizationName
    );
}
