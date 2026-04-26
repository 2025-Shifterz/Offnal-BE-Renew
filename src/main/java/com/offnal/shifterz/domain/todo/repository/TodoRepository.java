package com.offnal.shifterz.domain.todo.repository;

import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.domain.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {
    void deleteAllByOrganization(Organization organization);

    void deleteByMemberId(Long memberId);
}
