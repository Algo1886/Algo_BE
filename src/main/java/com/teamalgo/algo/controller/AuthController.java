package com.teamalgo.algo.controller;

import com.teamalgo.algo.dto.request.LoginRequest;
import com.teamalgo.algo.dto.response.TokenResponse;
import com.teamalgo.algo.dto.request.TokenRequest;
import com.teamalgo.algo.service.auth.AuthService;
import com.teamalgo.algo.service.auth.GithubOAuthService;
import com.teamalgo.algo.service.auth.GoogleOAuthService;
import com.teamalgo.algo.service.auth.KakaoOAuthService;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final GoogleOAuthService googleService;
    private final GithubOAuthService githubOAuthService;
    private final KakaoOAuthService kakaoOAuthService;
    private final AuthService authService;

    // 구글 로그인
    @PostMapping("/google-login")
    public ResponseEntity<ApiResponse<TokenResponse>> googleLogin(@RequestBody LoginRequest loginRequest) {
        TokenResponse response = googleService.authenticateUser(loginRequest.getToken());
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 카카오 로그인
    @PostMapping("/kakao-login")
    public ResponseEntity<ApiResponse<TokenResponse>> kakaoLogin(@RequestBody LoginRequest loginRequest) {
        TokenResponse response = kakaoOAuthService.authenticateUser(loginRequest.getToken());
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 깃허브 로그인
    @PostMapping("/github-login")
    public ResponseEntity<ApiResponse<TokenResponse>> githubLogin(@RequestBody LoginRequest loginRequest) {
        TokenResponse response = githubOAuthService.authenticateUser(loginRequest.getToken());
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // refresh token 재발급
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@RequestBody TokenRequest request) {
        TokenResponse response = authService.refresh(request.getRefreshToken());
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<TokenResponse>> logout(@RequestBody TokenRequest request, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        authService.logout(userId, request.getRefreshToken());
        return ApiResponse.success(SuccessCode._NO_CONTENT, null);
    }
}
