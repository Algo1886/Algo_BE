package com.teamalgo.algo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProblemDTO {
    private Long id;          // DB PK
    private String source;    // 문제 출처 (백준, 리트코드 등)
    private String displayId; // 숫자 기반 문제 번호 있으면 채움, slug 기반이면 null
    private String url;       // 문제 URL
    private String title;     // 문제 제목
}
