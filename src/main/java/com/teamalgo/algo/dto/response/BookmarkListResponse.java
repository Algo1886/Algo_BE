package com.teamalgo.algo.dto.response;

import com.teamalgo.algo.dto.RecordDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BookmarkListResponse {
    private List<RecordDTO> bookmarks;

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;


    public static BookmarkListResponse fromPage(org.springframework.data.domain.Page<RecordDTO> pageData) {
        return BookmarkListResponse.builder()
                .bookmarks(pageData.getContent())
                .page(pageData.getNumber() + 1) // 0-based → 1-based
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .first(pageData.isFirst())
                .last(pageData.isLast())
                .build();
    }
}
