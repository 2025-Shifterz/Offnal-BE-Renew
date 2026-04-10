package com.offnal.shifterz.domain.work.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_work_instance_calendar_date_type",
                columnNames = {"work_calendar_id", "work_date", "work_time_type"}
        )
})
public class WorkInstance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //근무 날짜
    @Column(name = "work_date")
    private LocalDate workDate;

    //근무 유형
    @Enumerated(EnumType.STRING)
    @Column(name = "work_time_type")
    private WorkTimeType workTimeType;

    //근무표와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_calendar_id")
    private WorkCalendar workCalendar;

    // ===== 메서드 =====

    public LocalDate date() { return this.workDate; }
    public WorkTimeType workType() { return this.workTimeType;  }

    public boolean isType(WorkTimeType workType) { return this.workTimeType == workType; }
    public boolean isOn(LocalDate date) { return Objects.equals(this.workDate, date); }


}
