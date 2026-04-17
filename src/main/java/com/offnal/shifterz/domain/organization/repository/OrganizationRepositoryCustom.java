package com.offnal.shifterz.domain.organization.repository;

import com.offnal.shifterz.domain.organization.domain.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepositoryCustom {
    List<Organization> findOrganizations(
            Long memberId,
            String organizationName,
            String team
    );

    Optional<Organization> findOrganization(
            Long memberId,
            String organizationName,
            String team
    );

    boolean existsOrganization(
            Long memberId,
            String organizationName,
            String team
    );
}
