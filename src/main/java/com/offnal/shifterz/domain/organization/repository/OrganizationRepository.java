package com.offnal.shifterz.domain.organization.repository;

import com.offnal.shifterz.domain.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>, OrganizationRepositoryCustom {
    void deleteByOrganizationMember_Id(Long memberId);
}
