package com.teamalgo.algo.controller;

import com.teamalgo.algo.dto.RecordDTO;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.security.CustomUserDetails;
import com.teamalgo.algo.service.record.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecordDTO>>> getRecommendedReviews (@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return ApiResponse.success(SuccessCode._OK, reviewService.getRecommendedReviews(userId));
    }

    @PostMapping("/{recordId}")
    public ResponseEntity<ApiResponse<Object>> completeReview (
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long recordId
    ) {
        Long userId = userDetails.getUser().getId();
        reviewService.completeReview(recordId, userId);
        return ApiResponse.success(SuccessCode._OK, null);
    }

}
