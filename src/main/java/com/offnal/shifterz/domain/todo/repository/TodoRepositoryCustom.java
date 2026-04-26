package com.offnal.shifterz.domain.todo.repository;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.todo.domain.Todo;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepositoryCustom {
    List<Todo> findTodosWithFilters(
            Member member, Long organizationId, boolean unassigned, LocalDate targetDate
    );
}
