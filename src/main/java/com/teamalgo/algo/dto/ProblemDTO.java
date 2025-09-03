package com.teamalgo.algo.dto;

import com.teamalgo.algo.domain.problem.Problem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProblemDTO {
    private Long id;
    private String source;
    private String url;
    private String title;  // API 제목

    public static ProblemDTO from(Problem problem) {
        return ProblemDTO.builder()
                .id(problem.getId())
                .source(problem.getSource())
                .url(problem.getUrl())
                .title(problem.getTitle())
                .build();
    }
}
