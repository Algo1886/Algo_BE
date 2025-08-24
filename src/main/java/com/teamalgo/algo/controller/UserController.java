package com.teamalgo.algo.controller;

import com.teamalgo.algo.dto.response.UserResponse;
import com.teamalgo.algo.dto.request.UserUpdateRequest;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        UserResponse response = userService.getUserInfo(userId);
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 사용자 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyInfo(Authentication authentication, @RequestBody UserUpdateRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        UserResponse response = userService.updateUser(userId, request);
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 사용자 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Object>> deleteUser(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        userService.deleteUser(userId);
        return ApiResponse.success(SuccessCode._NO_CONTENT, null);
    }
}
