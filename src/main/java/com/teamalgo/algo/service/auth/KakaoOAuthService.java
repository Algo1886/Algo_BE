package com.teamalgo.algo.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamalgo.algo.dto.response.TokenResponse;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final AuthService authService;
    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    @Value("${KAKAO_REDIRECT_URI_LOCAL}")
    private String kakaoRedirectUriLocal;

    @Value("${KAKAO_REDIRECT_URI_SERVER}")
    private String kakaoRedirectUriServer;

    @Value("${FRONT_ORIGIN_LOCAL}")
    private String frontOriginLocal;

    @Value("${FRONT_ORIGIN_SERVER}")
    private String frontOriginServer;

    @Transactional
    public TokenResponse authenticateUser(String code, String origin) {
        try {
            String redirectUri = resolveRedirectUri(origin);

            // 1) 인가 코드 → access_token 교환
            String tokenUrl = "https://kauth.kakao.com/oauth/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", kakaoClientId);
            params.add("redirect_uri", redirectUri);
            params.add("code", code);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
            ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, String.class);

            JsonNode tokenNode = objectMapper.readTree(tokenResponse.getBody());
            if (!tokenNode.has("access_token")) {
                log.error("카카오 access_token 누락");
                throw new CustomException(ErrorCode.OAUTH_FAILED);
            }
            String kakaoAccessToken = tokenNode.get("access_token").asText();

            // 2) access_token으로 사용자 정보 요청
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(kakaoAccessToken);
            userHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

            ResponseEntity<String> userResponse = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    new HttpEntity<>(userHeaders),
                    String.class
            );

            JsonNode root = objectMapper.readTree(userResponse.getBody());
            if (!root.hasNonNull("id")) {
                log.error("카카오 사용자 응답 오류: {}", userResponse.getBody());
                throw new CustomException(ErrorCode.OAUTH_FAILED);
            }

            String providerId = root.get("id").asText();

            // 3) 닉네임 결정 (재할당 대신 최종값 변수 하나로만 사용)
            String avatarUrl = root.path("kakao_account")
                    .path("profile")
                    .path("profile_image_url")
                    .asText(null);

            // 4) DB 저장 or 조회
            User user = userService.findByProviderAndProviderId("kakao", providerId)
                    .orElseGet(() -> userService.createUser("kakao", providerId, avatarUrl));

            // 5) JWT 발급
            return authService.issueTokens(user);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생", e);
            throw new CustomException(ErrorCode.OAUTH_FAILED);
        }
    }

    public String resolveRedirectUri(String origin) {
        if (origin == null) {
            throw new CustomException(ErrorCode.INVALID_REDIRECT_URI);
        } else if (origin.equals(frontOriginLocal)) {
            return kakaoRedirectUriLocal;
        } else if (origin.equals(frontOriginServer)) {
            return kakaoRedirectUriServer;
        } else {
            throw new CustomException(ErrorCode.INVALID_REDIRECT_URI);
        }
    }
}
