package com.offnal.shifterz.domain.log.repository;

import com.offnal.shifterz.domain.log.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LogRepository extends JpaRepository<Log, Long> {

    @Modifying
    @Query("UPDATE Log l SET l.member = null, l.anonymizedIdentifier = :anonymized WHERE l.member.id = :memberId")
    void anonymizeMemberLogs(@Param("memberId") Long memberId, @Param("anonymized") String anonymized);
}

