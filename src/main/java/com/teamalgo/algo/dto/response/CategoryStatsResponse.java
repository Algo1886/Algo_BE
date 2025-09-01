package com.teamalgo.algo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryStatsResponse {

    private String slug;
    private String name;
    private Long count;
    private Double ratio;

    public CategoryStatsResponse(String slug, String name, Long count) {
        this.slug = slug;
        this.name = name;
        this.count = count;
        this.ratio = 0.0;
    }

}
