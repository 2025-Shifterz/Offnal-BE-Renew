package com.offnal.shifterz.domain.member.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(
	name = "member",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_provider_provider_id",
			columnNames = {"provider", "provider_id"}
		)
	}
)
public class Member extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Provider provider;

	@Column(name = "provider_id", nullable = false)
	private String providerId;

	private String email;
	private String memberName;
	private String phoneNumber;

	private String profileImageKey;

	private String appleRefreshToken;

	public void updateMemberInfo(String encryptedMemberName, String profileImageKey) {
		this.memberName = encryptedMemberName;
		this.profileImageKey = profileImageKey;
	}

	public void updateAppleRefreshToken(String encryptedAppleRefreshToken) {
		this.appleRefreshToken = encryptedAppleRefreshToken;
	}

	//마이그레이션 후 삭제
	public void migrateSensitiveFields(String emailEnc, String nameEnc, String phoneEnc, String appleRefreshTokenEnc) {
		this.email = emailEnc;
		this.memberName = nameEnc;
		this.phoneNumber = phoneEnc;
		this.appleRefreshToken = appleRefreshTokenEnc;
	}
}
