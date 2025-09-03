package com.teamalgo.algo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProblemPreviewResponse {
    private String title;   // API 제목
    private String source;
    private String url;
}
