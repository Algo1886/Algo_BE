package com.teamalgo.algo.controller;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.CoreIdeaDTO;
import com.teamalgo.algo.dto.RecordDTO;
import com.teamalgo.algo.dto.response.CategoryStatsResponse;
import com.teamalgo.algo.dto.response.DashboardResponse;
import com.teamalgo.algo.dto.response.StreakCalendarResponse;
import com.teamalgo.algo.dto.response.UserStatsResponse;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.security.CustomUserDetails;
import com.teamalgo.algo.service.record.CoreIdeaService;
import com.teamalgo.algo.service.record.ReviewService;
import com.teamalgo.algo.service.stats.CategoryStatsService;
import com.teamalgo.algo.service.stats.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class StatsController {

    private final StatsService statsService;
    private final ReviewService reviewService;
    private final CoreIdeaService coreIdeaService;
    private final CategoryStatsService categoryStatsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();

        DashboardResponse statsPart = statsService.getDashboard(user);

        List<RecordDTO> recommendations = reviewService.getRecommendedReviews(user.getId()).stream().limit(2).toList();

        List<CoreIdeaDTO> recentIdeas = coreIdeaService.getRecentIdeas(user.getId(), 2);

        DashboardResponse response = statsPart.toBuilder()
                .recommendations(recommendations)
                .recentIdeas(recentIdeas)
                .build();

        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 스트릭 조회
    @GetMapping("/streak")
    public ResponseEntity<ApiResponse<StreakCalendarResponse>> getStreak(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        StreakCalendarResponse response = statsService.getYearlyStreak(userId);
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 사용자 통계 조회
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getStats (@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        UserStatsResponse response = statsService.getUserStats(userId);
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 유형 분포 조회
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryStatsResponse>>> getCategoryStats(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        List<CategoryStatsResponse> response = categoryStatsService.getCategoryStats(userId);
        return ApiResponse.success(SuccessCode._OK, response);
    }

}
