package com.offnal.shifterz.core.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
	private final CustomUserDetailsService customUserDetailsService;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String token = jwtTokenProvider.resolveToken(request);

		if (token != null) {
			if (!jwtTokenProvider.validateToken(token) || tokenService.isBlacklisted(token)) {
				handleAuthError(request, response);
				return;
			}

			Long memberId = jwtTokenProvider.getMemberId(token);
			CustomUserDetails userDetails = customUserDetailsService.loadByMemberId(memberId);

			Authentication authentication = new UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.getAuthorities()
			);

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private void handleAuthError(
		HttpServletRequest request,
		HttpServletResponse response
	) throws IOException, ServletException {
		authenticationEntryPoint.commence(
			request,
			response,
			new AuthenticationException("유효하지 않은 토큰입니다.") {
			}
		);
	}
}
