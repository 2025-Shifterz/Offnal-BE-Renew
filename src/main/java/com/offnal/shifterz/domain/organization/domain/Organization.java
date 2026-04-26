package com.offnal.shifterz.domain.organization.domain;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.work.domain.WorkCalendar;
import com.offnal.shifterz.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "organization",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_org_member_name_team",
                        columnNames = {
                                "member_id", "organization_name", "team"
                        }
                )
        }
)
public class Organization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Column(name = "team", nullable = false)
    private String team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member organizationMember;

    @OneToMany(mappedBy = "organization",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @Builder.Default
    private List<WorkCalendar> calendars = new ArrayList<>();

    public void rename(String name, String team) {
        this.organizationName = name;
        this.team = team;
    }

    public String name() { return this.organizationName; }
    public String team() { return this.team; }

    public boolean isNamed(String name, String team) {
        return Objects.equals(this.organizationName, name) && Objects.equals(this.team, team);
    }

}

