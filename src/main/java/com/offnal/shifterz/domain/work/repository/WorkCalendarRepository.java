package com.offnal.shifterz.domain.work.repository;

import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.work.domain.WorkCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkCalendarRepository extends JpaRepository<WorkCalendar, Long> {
    Optional<WorkCalendar> findByIdAndMemberIdAndOrganization(Long calendarId, Long memberId, Organization organization);

    boolean existsByMemberIdAndOrganization(Long memberId, Organization organization);

    Optional<WorkCalendar> findByMemberIdAndOrganization(Long memberId, Organization organization);

    List<WorkCalendar> findByMemberIdAndOrganizationOrderByIdDesc(Long memberId, Organization organization);

    @Transactional
    @Modifying
    void deleteByMemberId(Long memberId);
}
