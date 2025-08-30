package com.teamalgo.algo.controller;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.RecordDTO;
import com.teamalgo.algo.dto.response.BookmarkListResponse;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.SuccessCode;
import com.teamalgo.algo.security.CustomUserDetails;
import com.teamalgo.algo.service.record.BookmarkService;
import com.teamalgo.algo.service.user.UserService;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final UserService userService;

    // 북마크 추가
    @PostMapping("/{recordId}")
    public ResponseEntity<ApiResponse<Void>> addBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long recordId
    ) {
        User user = userService.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        bookmarkService.addBookmark(user, recordId);
        return ApiResponse.success(SuccessCode._CREATED, null);
    }

    // 북마크 삭제
    @DeleteMapping("/{recordId}")
    public ResponseEntity<ApiResponse<Void>> removeBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long recordId
    ) {
        User user = userService.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        bookmarkService.removeBookmark(user, recordId);
        return ApiResponse.success(SuccessCode._OK, null);
    }

    // 북마크 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<BookmarkListResponse>> getMyBookmarks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = userDetails.getUser();

        Page<RecordDTO> bookmarks = bookmarkService.getBookmarkedRecords(user, PageRequest.of(page, size));
        BookmarkListResponse response = BookmarkListResponse.fromPage(bookmarks);

        return ApiResponse.success(SuccessCode._OK, response);
    }


}
