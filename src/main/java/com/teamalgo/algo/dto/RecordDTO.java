package com.teamalgo.algo.dto;

import com.teamalgo.algo.domain.record.Record;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class RecordDTO {
    private Long id;
    private String title;       // customTitle 우선, 없으면 problem.title
    private List<String> categories;
    private String author;
    private String source;

    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static RecordDTO from(Record record) {
        String finalTitle = (record.getCustomTitle() != null && !record.getCustomTitle().isBlank())
                ? record.getCustomTitle()
                : record.getProblem().getTitle();

        return RecordDTO.builder()
                .id(record.getId())
                .title(finalTitle)
                .categories(record.getRecordCategories().stream()
                        .map(rc -> rc.getCategory().getName())
                        .toList())
                .author(record.getUser().getUsername())
                .source(record.getProblem().getSource())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
