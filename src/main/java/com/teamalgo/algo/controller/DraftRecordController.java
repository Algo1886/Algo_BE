package com.teamalgo.algo.controller;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.response.RecordListResponse;
import com.teamalgo.algo.dto.response.RecordResponse;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.security.CustomUserDetails;
import com.teamalgo.algo.service.record.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/drafts")
public class DraftRecordController {

    private final RecordService recordService;

    // Draft 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<RecordListResponse.Data>> getMyDrafts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = userDetails.getUser();
        Page<com.teamalgo.algo.domain.record.Record> drafts = recordService.getDraftsByUser(user, PageRequest.of(page, size));
        RecordListResponse.Data response = recordService.createRecordListResponse(drafts);
        return ApiResponse.success(SuccessCode._OK, response);
    }
}
