package com.teamalgo.algo.auth.controller;

import com.teamalgo.algo.auth.dto.LoginRequest;
import com.teamalgo.algo.auth.dto.TokenResponse;
import com.teamalgo.algo.auth.service.GithubOAuthService;
import com.teamalgo.algo.auth.service.GoogleOAuthService;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final GoogleOAuthService googleService;
    private final GithubOAuthService githubOAuthService;

    // 구글 로그인
    @PostMapping("/google-login")
    public ResponseEntity<ApiResponse<TokenResponse>> googleLogin(@RequestBody LoginRequest loginRequest) {
        TokenResponse response = googleService.authenticateUser(loginRequest.getToken());
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 카카오 로그인
    @PostMapping("/kakao-login")
    public ResponseEntity<ApiResponse<TokenResponse>> kakaoLogin(@RequestBody LoginRequest loginRequest) {

        return ApiResponse.success(SuccessCode._OK, null);
    }

    // 깃허브 로그인
    @PostMapping("/github-login")
    public ResponseEntity<ApiResponse<TokenResponse>> githubLogin(@RequestBody LoginRequest loginRequest) {
        TokenResponse response = githubOAuthService.authenticateUser(loginRequest.getToken());
        return ApiResponse.success(SuccessCode._OK, response);
    }

}