package com.teamalgo.algo.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamalgo.algo.auth.dto.TokenResponse;
import com.teamalgo.algo.auth.security.JwtTokenProvider;
import com.teamalgo.algo.user.domain.User;
import com.teamalgo.algo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public TokenResponse authenticateUser(String kakaoAccessToken) {
        try {
            // 1) 전달 토큰 로그
            System.out.println("💡 전달받은 카카오 Access Token: " + kakaoAccessToken);

            // 2) 카카오 사용자 정보 요청
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + kakaoAccessToken);
            headers.set("Accept", "application/json"); // GET엔 Accept가 더 적절

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            String raw = response.getBody();
            System.out.println("📌 카카오 raw 응답: " + raw);

            // 3) JSON 파싱 (NPE 방지)
            JsonNode root = objectMapper.readTree(raw);

            if (!root.hasNonNull("id")) {
                throw new RuntimeException("카카오 응답에 id가 없습니다.");
            }
            String providerId = root.get("id").asText();

            String nickname = null;
            if (root.has("properties") && root.get("properties").has("nickname")) {
                nickname = root.get("properties").get("nickname").asText(null);
            }
            if (nickname == null && root.has("kakao_account")
                    && root.get("kakao_account").has("profile")
                    && root.get("kakao_account").get("profile").has("nickname")) {
                nickname = root.get("kakao_account").get("profile").get("nickname").asText(null);
            }
            if (nickname == null || nickname.isBlank()) nickname = "카카오유저";

            final String finalNickname = nickname;

            // 4) DB 조회/저장
            User user = userService.findByProviderAndProviderId("kakao", providerId)
                    .orElseGet(() -> userService.saveUser(User.builder()
                            .nickname(finalNickname)
                            .provider("kakao")
                            .providerId(providerId)
                            .build()));

            // 5) JWT 발급
            String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()));
            String refreshToken = jwtTokenProvider.generateRefreshToken(String.valueOf(user.getId()));

            // 6) 응답
            return new TokenResponse(accessToken, refreshToken);

        } catch (Exception e) {
            // 원인 추적 쉽게 로그 남기기
            e.printStackTrace();
            throw new RuntimeException("카카오 로그인 처리 중 오류: " + e.getMessage(), e);
        }
    }
}
