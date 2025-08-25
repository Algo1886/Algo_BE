package com.teamalgo.algo.service.auth;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.TokenResponse;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;

    public TokenResponse issueTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());

        refreshTokenRedisService.saveRefreshToken(user.getId(), refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    public void logout(Long userId, String refreshToken) {
        String stored = refreshTokenRedisService.getRefreshToken(userId);

        if (!userId.equals(jwtTokenProvider.getUserIdFromToken(refreshToken))
                || !jwtTokenProvider.validateToken(refreshToken)
                || (stored == null || !stored.equals(refreshToken)
        )) {
            throw new CustomException(ErrorCode.JWT_TOKEN_INVALID);
        }

        refreshTokenRedisService.deleteRefreshToken(userId);
    }

    public TokenResponse refresh(String refreshToken) {
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String storedToken = refreshTokenRedisService.getRefreshToken(userId);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.JWT_TOKEN_INVALID);
        }

        jwtTokenProvider.validateToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId.toString());

        return new TokenResponse(newAccessToken, refreshToken);
    }

}
