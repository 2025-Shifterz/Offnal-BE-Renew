package com.offnal.shifterz.domain.work.repository;

import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.work.domain.WorkCalendar;

import java.util.Optional;

public interface WorkCalendarRepositoryCustom {
    Optional<WorkCalendar> findCalendar(Long calendarId, Long memberId, Organization organization);
}
