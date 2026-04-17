package com.offnal.shifterz.domain.work.repository;

import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.work.domain.WorkInstance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkInstanceRepositoryCustom {
    List<WorkInstance> findByOrganizationIdAndDateRange(
            Long organizationId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<WorkInstance> findWorkInstance(
            Long memberId,
            LocalDate workDate,
            Organization organization  // null 허용
    );

    List<WorkInstance> findWorkInstances(
            Long memberId,
            Organization organization,
            LocalDate startDate,
            LocalDate endDate
    );
}
