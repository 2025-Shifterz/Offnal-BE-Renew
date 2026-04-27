package com.offnal.shifterz.core.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.offnal.shifterz.core.config.JwtProperties;
import com.offnal.shifterz.global.exception.CommonErrorCode;
import com.offnal.shifterz.global.exception.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	private final JwtProperties jwtProperties;

	private Key secretKey;

	@PostConstruct
	protected void init() {
		byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.secret());
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public String createAccessToken(Long memberId) {
		return createToken(memberId, jwtProperties.accessTokenValiditySecond());
	}

	public String createRefreshToken(Long memberId) {
		return createToken(memberId, jwtProperties.refreshTokenValiditySecond());
	}

	private String createToken(Long memberId, long validitySeconds) {
		Date now = new Date();

		return Jwts.builder()
			.setSubject(String.valueOf(memberId))
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + validitySeconds * 1000))
			.signWith(secretKey)
			.compact();
	}

	public Long getMemberId(String token) {
		Claims claims = parseClaims(token);
		return Long.parseLong(claims.getSubject());
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			return false;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");

		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}

		return null;
	}

	public long getExpiration(String token) {
		try {
			Claims claims = parseClaims(token);
			long remaining = claims.getExpiration().getTime() - System.currentTimeMillis();

			return Math.max(remaining, 0L);
		} catch (ExpiredJwtException e) {
			return 0L;
		} catch (Exception e) {
			throw new CustomException(CommonErrorCode.INVALID_REQUEST);
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}