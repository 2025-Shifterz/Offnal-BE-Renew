package com.offnal.shifterz.domain.member.domain;

import com.offnal.shifterz.global.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
