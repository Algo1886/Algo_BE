package com.teamalgo.algo.service.auth;

import com.teamalgo.algo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    // refresh token 저장
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = generateKey(userId);

        // 토큰 만료 시간 계산
        LocalDateTime expiration = jwtTokenProvider.getExpiration(refreshToken);
        Duration ttl = Duration.between(LocalDateTime.now(), expiration);

        redisTemplate.opsForValue().set(key, refreshToken, ttl);
    }

    // refresh token 조회
    public String getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get(generateKey(userId));
    }

    // refresh token 삭제 (로그아웃)
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(generateKey(userId));
    }

    private String generateKey(Long userId) {
        return "refresh:" + userId;
    }
}
