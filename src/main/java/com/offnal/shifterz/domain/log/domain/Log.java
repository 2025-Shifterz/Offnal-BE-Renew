package com.offnal.shifterz.domain.log.domain;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Log extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 로그 액션 구분
     * C = Controller Enter
     * R = Controller Return
     * S = Service Enter
     * T = Service Return
     * E = Error
     */
    private Character action;

    private LocalDateTime time;

    @Lob
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = true)
    private Member member;

    @Column(nullable = true)
    private String anonymizedIdentifier;

    @Builder
    private Log(Member member, Character action, LocalDateTime time, String message, String anonymizedIdentifier) {
        this.member = member;
        this.action = action;
        this.time = time;
        this.message = message;
        this.anonymizedIdentifier = anonymizedIdentifier;
    }

    /**
     * 로그 메시지 갱신
     */
    public void updateMessage(String newMessage) {
        this.message = newMessage;
        this.time = LocalDateTime.now(); // 수정 시각도 갱신
    }

    /**
     * action 값만 바꾸고 싶을 때
     */
    public void updateAction(Character newAction) {
        this.action = newAction;
        this.time = LocalDateTime.now();
    }
}

