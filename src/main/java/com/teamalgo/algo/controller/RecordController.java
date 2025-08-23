package com.teamalgo.algo.controller;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.request.RecordCreateRequest;
import com.teamalgo.algo.dto.request.RecordSearchRequest;
import com.teamalgo.algo.dto.request.RecordUpdateRequest;
import com.teamalgo.algo.dto.response.RecordListResponse;
import com.teamalgo.algo.dto.response.RecordResponse;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.service.record.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    // 레코드 생성
    @PostMapping
    public ResponseEntity<ApiResponse<RecordResponse.Data>> createRecord(
            Authentication authentication,
            @RequestBody RecordCreateRequest request
    ) {
        User user = (User) authentication.getPrincipal();
        var record = recordService.createRecord(user, request);
        var response = recordService.createRecordResponse(record);
        return ApiResponse.success(SuccessCode._CREATED, response);
    }

    // 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecordResponse.Data>> getRecord(@PathVariable Long id) {
        var record = recordService.getRecordById(id);
        var response = recordService.createRecordResponse(record);
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 목록 조회 (검색)
    @GetMapping
    public ResponseEntity<ApiResponse<RecordListResponse.Data>> searchRecords(RecordSearchRequest request) {
        Page<com.teamalgo.algo.domain.record.Record> records = recordService.searchRecords(request);
        RecordListResponse.Data response = recordService.createRecordListResponse(records);
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 업데이트
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RecordResponse.Data>> patchRecord(
            @PathVariable Long id,
            @RequestBody RecordUpdateRequest request
    ) {
        var record = recordService.patchRecord(id, request);
        var response = recordService.createRecordResponse(record);
        return ApiResponse.success(SuccessCode._OK, response);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ApiResponse.success(SuccessCode._OK, null);
    }
}
