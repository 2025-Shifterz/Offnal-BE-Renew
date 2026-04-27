package com.offnal.shifterz.core.jwt;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.offnal.shifterz.domain.member.domain.Member;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {

	private final Member member;
	private final Long id;
	private final String username;
	private final Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails(Member member) {
		this.member = member;
		this.id = member.getId();

		// Spring Security에서 식별자로 사용할 값
		this.username = String.valueOf(member.getId());

		this.authorities = Collections.singletonList(
			new SimpleGrantedAuthority("ROLE_USER")
		);
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
