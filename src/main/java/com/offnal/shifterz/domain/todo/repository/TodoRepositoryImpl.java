package com.offnal.shifterz.domain.todo.repository;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.todo.domain.QTodo;
import com.offnal.shifterz.domain.todo.domain.Todo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Todo> findTodosWithFilters(
            Member member,
            Long organizationId,
            boolean unassigned,
            LocalDate targetDate
    ) {
        QTodo todo = QTodo.todo;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(todo.member.eq(member));
        builder.and(todo.targetDate.year().eq(targetDate.getYear())
                .and(todo.targetDate.month().eq(targetDate.getMonthValue()))
                .and(todo.targetDate.dayOfMonth().eq(targetDate.getDayOfMonth())));

        if (organizationId != null) {
            builder.and(todo.organization.id.eq(organizationId));
        }
        if (unassigned) {
            builder.and(todo.organization.isNull());
        }

        return queryFactory.selectFrom(todo).where(builder).fetch();
    }
}
