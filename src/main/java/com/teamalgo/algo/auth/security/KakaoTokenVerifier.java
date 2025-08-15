package com.teamalgo.algo.auth.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamalgo.algo.auth.dto.KakaoUserInfo;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class KakaoTokenVerifier {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${KAKAO_USERINFO_URL:https://kapi.kakao.com/v2/user/me}")
    private String kakaoUserInfoUrl;

    public KakaoUserInfo verifyToken(String accessToken) {
        try {
            URL url = new URL(kakaoUserInfoUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            if (conn.getResponseCode() != 200) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            JsonNode jsonNode = objectMapper.readTree(conn.getInputStream());

            Long id = jsonNode.get("id").asLong();
            String nickname = jsonNode.path("kakao_account")
                    .path("profile")
                    .path("nickname")
                    .asText();

            return new KakaoUserInfo(String.valueOf(id), nickname);

        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
