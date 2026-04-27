package com.offnal.shifterz.core.jwt;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.offnal.shifterz.core.config.JwtProperties;
import com.offnal.shifterz.core.jwt.exception.TokenErrorCode;
import com.offnal.shifterz.domain.member.repository.RefreshTokenRepository;
import com.offnal.shifterz.global.AuthContext;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.util.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RedisUtil redisUtil;
	private final JwtProperties jwtProperties;

	public TokenDto.TokenResponse reissue(TokenDto.TokenReissueRequest request) {
		String oldRefreshToken = request.getRefreshToken();

		if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
			throw new CustomException(TokenErrorCode.INVALID_REFRESH_TOKEN);
		}

		Long memberId = jwtTokenProvider.getMemberId(oldRefreshToken);

		String storedToken = refreshTokenRepository.findByMemberId(memberId);

		if (storedToken == null || !storedToken.equals(oldRefreshToken)) {
			throw new CustomException(TokenErrorCode.INVALID_REFRESH_TOKEN);
		}

		String newAccessToken = jwtTokenProvider.createAccessToken(memberId);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

		refreshTokenRepository.save(
			memberId,
			newRefreshToken,
			jwtProperties.refreshTokenValiditySecond(),
			TimeUnit.SECONDS
		);

		return new TokenDto.TokenResponse(newAccessToken, newRefreshToken);
	}

	public void logout(String accessToken) {
		if (accessToken == null || !jwtTokenProvider.validateToken(accessToken)) {
			throw new CustomException(TokenErrorCode.INVALID_TOKEN);
		}

		Long memberId = jwtTokenProvider.getMemberId(accessToken);

		refreshTokenRepository.delete(memberId);
		blacklistAccessToken(accessToken);
	}

	public void blacklistAccessToken(String accessToken) {
		if (accessToken == null || accessToken.isBlank()) {
			throw new CustomException(TokenErrorCode.INVALID_TOKEN);
		}

		long expiration = jwtTokenProvider.getExpiration(accessToken);

		if (expiration <= 0) {
			throw new CustomException(TokenErrorCode.INVALID_TOKEN);
		}

		redisUtil.setBlackList(accessToken, expiration, TimeUnit.MILLISECONDS);
	}

	public boolean isBlacklisted(String accessToken) {
		return redisUtil.isBlackListed(accessToken);
	}
}