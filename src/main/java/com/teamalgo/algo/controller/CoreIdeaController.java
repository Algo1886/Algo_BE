package com.teamalgo.algo.controller;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.CoreIdeaDTO;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.service.record.CoreIdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class CoreIdeaController {

    private final CoreIdeaService coreIdeaService;

    // 내 아이디어 전체 조회
    @GetMapping("/ideas")
    public ResponseEntity<ApiResponse<List<CoreIdeaDTO>>> getMyIdeas(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<CoreIdeaDTO> ideas = coreIdeaService.getUserIdeas(user.getId());
        return ApiResponse.success(SuccessCode._OK, ideas);
    }
}
