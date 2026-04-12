package com.offnal.shifterz.domain.memberOrganizationTeam.domain;

// package com.xxx.domain.organization.entity;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.organization.domain.Organization;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberOrganizationTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Organization organization;

    @Column(nullable = false, length = 50)
    private String team;

    public void updateTeam(String team) {
        this.team = team;
    }
}
