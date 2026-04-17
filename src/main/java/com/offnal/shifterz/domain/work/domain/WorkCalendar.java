package com.offnal.shifterz.domain.work.domain;

import com.offnal.shifterz.domain.organization.domain.Organization;
import com.offnal.shifterz.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class WorkCalendar extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "work_times", joinColumns = @JoinColumn(name = "work_calendar_id"))
    @MapKeyColumn(name = "time_symbol")
    private Map<String, WorkTime> workTimes = new HashMap<>();

    @OneToMany(mappedBy = "workCalendar",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<WorkInstance> workInstances = new ArrayList<>();

    // 동시성 제어 - 낙관적 락
    @Version
    private Long version;

    // ===== 메서드 =====

    public Long id() { return this.id; }
    public Map<String,WorkTime> workTimes() { return this.workTimes; }

    // 해당 회원 및 조직 소유 캘린더인지
    public boolean isOwnedBy(Long memberId, Organization org) {
        if (this.memberId == null || memberId == null) return false;
        if (this.organization == null || org == null) return false;
        return Objects.equals(this.memberId, memberId)
                && Objects.equals(this.organization.getId(), org.getId());
    }

    public void putWorkTime(String symbol, WorkTime time) { this.workTimes.put(symbol, time); }

}