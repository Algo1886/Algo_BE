package com.teamalgo.algo.controller;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.CoreIdeaDTO;
import com.teamalgo.algo.dto.response.CoreIdeaListResponse;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.security.CustomUserDetails;
import com.teamalgo.algo.service.record.CoreIdeaService;
import com.teamalgo.algo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class CoreIdeaController {

    private final CoreIdeaService coreIdeaService;
    private final UserService userService;

    // 내 아이디어 전체 조회
    @GetMapping("/ideas")
    public ResponseEntity<ApiResponse<CoreIdeaListResponse>> getMyIdeas(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category
    ) {
        User user = userService.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<CoreIdeaDTO> ideas;
        if (category == null || category.isBlank()) {
            ideas = coreIdeaService.getUserIdeas(user.getId(), PageRequest.of(page, size));
        } else {
            ideas = coreIdeaService.getUserIdeas(user.getId(), PageRequest.of(page, size), category);
        }

        CoreIdeaListResponse response = CoreIdeaListResponse.fromPage(ideas);
        return ApiResponse.success(SuccessCode._OK, response);
    }



}
