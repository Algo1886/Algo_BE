package com.teamalgo.algo.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamalgo.algo.dto.response.TokenResponse;
import com.teamalgo.algo.security.JwtTokenProvider;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GithubOAuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${GITHUB_CLIENT_ID}")
    private String clientId;

    @Value("${GITHUB_CLIENT_SECRET}")
    private String clientSecret;

    @Transactional
    public TokenResponse authenticateUser(String code) {
        try {
            // github에 access token 요청
            RestTemplate restTemplate = new RestTemplate();
            String tokenUrl = "https://github.com/login/oauth/access_token";

            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
            tokenHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

            Map<String, String> body = Map.of(
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "code", code
            );

            HttpEntity<Map<String, String>> tokenRequest = new HttpEntity<>(body, tokenHeaders);

            ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, String.class);

            Map<String, Object> tokenMap = objectMapper.readValue(tokenResponse.getBody(), Map.class);

            if (!tokenMap.containsKey("access_token")) {
                throw new CustomException(ErrorCode.OAUTH_FAILED);
            }

            String accessTokenGithub = (String) tokenMap.get("access_token");

            // access token으로 사용자 정보 요청
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessTokenGithub);
            userHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

            ResponseEntity<String> userResponse = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    userRequest,
                    String.class
            );

            Map<String, Object> userMap = objectMapper.readValue(userResponse.getBody(), Map.class);

            String providerId = String.valueOf(userMap.get("id"));
            String avatarUrl = (String) userMap.get("avatar_url");

            // DB에 사용자 저장 or 기존 사용자 조회
            User user = userService.findByProviderAndProviderId("github", providerId)
                    .orElseGet(() -> userService.createUser("github", providerId, avatarUrl));

            // JWT 발급
            String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()));
            String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(user.getId()));

            return new TokenResponse(accessToken, refreshToken);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.OAUTH_FAILED);
        }
    }
}