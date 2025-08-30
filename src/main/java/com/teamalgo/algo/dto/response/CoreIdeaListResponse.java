package com.teamalgo.algo.dto.response;

import com.teamalgo.algo.dto.CoreIdeaDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CoreIdeaListResponse {

    private List<CoreIdeaDTO> ideas;

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static CoreIdeaListResponse fromPage(org.springframework.data.domain.Page<CoreIdeaDTO> pageData) {
        return CoreIdeaListResponse.builder()
                .ideas(pageData.getContent())
                .page(pageData.getNumber() + 1) // 0-based → 1-based
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .first(pageData.isFirst())
                .last(pageData.isLast())
                .build();
    }
}
