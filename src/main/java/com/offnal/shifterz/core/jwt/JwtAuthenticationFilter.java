package com.offnal.shifterz.core.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.offnal.shifterz.core.jwt.exception.JwtAuthException;
import com.offnal.shifterz.core.jwt.exception.TokenErrorCode;
import com.offnal.shifterz.global.exception.CustomAuthenticationEntryPoint;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final TokenService tokenService;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String token = jwtTokenProvider.resolveToken(request);

		if (token != null) {
			try {
				// 1. 토큰 유효성 검증
				if (!jwtTokenProvider.validateToken(token)) {
					throw new JwtAuthException(TokenErrorCode.INVALID_TOKEN);
				}

				// 2. 로그아웃된 토큰인지 확인
				if (tokenService.isBlacklisted(token)) {
					throw new JwtAuthException(TokenErrorCode.LOGOUT_TOKEN);
				}

				// 3. 인증 객체 생성 - DB 조회 없이 토큰 클레임만으로 생성
				Long memberId = jwtTokenProvider.getMemberId(token);

				SecurityContextHolder.getContext().setAuthentication(
					new UsernamePasswordAuthenticationToken(
						memberId,
						null,
						List.of(new SimpleGrantedAuthority("ROLE_USER"))
					)
				);

			} catch (JwtAuthException e) {
				SecurityContextHolder.clearContext();
				authenticationEntryPoint.commence(
					request,
					response,
					new AuthenticationException(e.getMessage()) {}
				);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}
}
