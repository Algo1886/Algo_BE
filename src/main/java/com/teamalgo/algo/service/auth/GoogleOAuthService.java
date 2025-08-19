package com.teamalgo.algo.service.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.teamalgo.algo.dto.TokenResponse;
import com.teamalgo.algo.security.GoogleTokenVerifier;
import com.teamalgo.algo.security.JwtTokenProvider;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Transactional
    public TokenResponse authenticateUser(String token) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verifyToken(token);

        String providerId = payload.getSubject();
        String avatarUrl = (String) payload.get("picture"); // 구글 프로필 이미지

        User user = userService.findByProviderAndProviderId("google", providerId)
                .orElseGet(() -> userService.createUser("google", providerId, avatarUrl));

        String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(user.getId()));
        return new TokenResponse(accessToken, refreshToken);
    }
}
