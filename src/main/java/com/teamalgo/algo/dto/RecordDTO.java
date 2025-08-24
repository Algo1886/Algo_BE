package com.teamalgo.algo.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.teamalgo.algo.domain.record.Record;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class RecordDTO {

    private Long id;           // 풀이 기록 ID
    private String title;      // 문제 제목
    private List<String> categories; // 문제 카테고리 리스트
    private String author;     // 작성자 닉네임
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
