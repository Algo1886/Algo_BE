package com.teamalgo.algo.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProblemDTO {
    private Long id;        // 문제 ID
    private String source;  // 문제 출처 (boj, leetcode 등)
    private String externalId; // 출처 내 문제 식별자
    private String url;     // 문제 URL
    private String title;   // 문제 제목
}