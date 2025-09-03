package com.teamalgo.algo.controller;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.request.RecordCreateRequest;
import com.teamalgo.algo.dto.request.RecordSearchRequest;
import com.teamalgo.algo.dto.request.RecordUpdateRequest;
import com.teamalgo.algo.dto.response.RecordListResponse;
import com.teamalgo.algo.dto.response.RecordResponse;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.security.CustomUserDetails;
import com.teamalgo.algo.service.record.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    // 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecordResponse.Data>> getRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        var record = recordService.getRecordById(id);
        var response = recordService.createRecordResponse(record, user);
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<RecordListResponse>> searchRecords(
            RecordSearchRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails
    ) {
        boolean isAuthenticated = (userDetails != null);

        Page<com.teamalgo.algo.domain.record.Record> records =
                recordService.searchRecords(request, isAuthenticated);

        RecordListResponse response = recordService.createRecordListResponse(records);
        return ApiResponse.success(SuccessCode._OK, response);
    }
    // 생성
    @PostMapping
    public ResponseEntity<ApiResponse<RecordResponse.Data>> createRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RecordCreateRequest request
    ) {
        User user = userDetails.getUser();
        var record = recordService.createRecord(user, request);
        var response = recordService.createRecordResponse(record, user);
        return ApiResponse.success(SuccessCode._CREATED, response);
    }

    // 수정 (블로그 전체 교체)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecordResponse.Data>> updateRecord(
            @PathVariable Long id,
            @RequestBody RecordUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        var record = recordService.updateRecord(id, request, user);
        var response = recordService.createRecordResponse(record, user);
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        recordService.deleteRecord(id, user);
        return ApiResponse.success(SuccessCode._OK, null);
    }
}
