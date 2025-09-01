package com.teamalgo.algo.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.teamalgo.algo.domain.record.Record;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class RecordDTO {
    private Long id;
    private String title;
    private List<String> categories;
    private String author;

    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static RecordDTO from(Record record) {
        return RecordDTO.builder()
                .id(record.getId())
                .title(record.getProblem().getTitle())
                .categories(record.getRecordCategories().stream()
                        .map(rc -> rc.getCategory().getName())
                        .toList())
                .author(record.getUser().getUsername())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
