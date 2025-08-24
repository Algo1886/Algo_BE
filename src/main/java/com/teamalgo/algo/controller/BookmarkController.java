package com.teamalgo.algo.controller;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.RecordDTO;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.service.record.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 북마크 추가
    @PostMapping("/{recordId}")
    public ResponseEntity<ApiResponse<Void>> addBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable Long recordId
    ) {
        bookmarkService.addBookmark(user, recordId);
        return ApiResponse.success(SuccessCode._CREATED, null);
    }

    // 북마크 삭제
    @DeleteMapping("/{recordId}")
    public ResponseEntity<ApiResponse<Void>> removeBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable Long recordId
    ) {
        bookmarkService.removeBookmark(user, recordId);
        return ApiResponse.success(SuccessCode._OK, null);
    }

    // 북마크 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<RecordDTO>>> getBookmarks(
            @AuthenticationPrincipal User user
    ) {
        var response = bookmarkService.getBookmarkedRecords(user);
        return ApiResponse.success(SuccessCode._OK, response);
    }
}
