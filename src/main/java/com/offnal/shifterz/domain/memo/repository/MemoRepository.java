package com.offnal.shifterz.domain.memo.repository;

import com.offnal.shifterz.domain.memo.domain.Memo;
import com.offnal.shifterz.domain.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long>, MemoRepositoryCustom {

    void deleteByMemberId(Long memberId);
    void deleteAllByOrganization(Organization organization);
}
