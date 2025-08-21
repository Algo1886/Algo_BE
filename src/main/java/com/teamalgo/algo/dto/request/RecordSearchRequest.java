package com.teamalgo.algo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RecordSearchRequest {

    @Min(value = 1, message = "Page number must be at least 1")
    @Builder.Default
    private Integer page = 1;   // 페이지 번호 (기본값 1)

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot be greater than 100")
    @Builder.Default
    private Integer size = 20;  // 페이지 크기 (기본값 20, 최대 100)

    @Builder.Default
    private SortType sort = SortType.LATEST; // 정렬 기준 (LATEST, POPULAR)

    private String category;  // 문제 카테고리 (예: DFS, BFS)

    private String search;    // 검색어

    private String author;    // 작성자 handle (로그인 안 한 경우 무시)

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;  // 조회 시작일

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;    // 조회 종료일

    public enum SortType {
        LATEST, POPULAR
    }

    // 날짜 범위가 유효한지 확인하는 메서드
    public boolean isValidDateRange() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }

    //page index (0-based) 변환 메서드 → Pageable 생성 시 사용
    public int getPageIndex() {
        return (page != null && page > 0) ? page - 1 : 0;
    }
}
