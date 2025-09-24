package com.teamalgo.algo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.teamalgo.algo.domain.record.RecordCoreIdea;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreIdeaDTO {

    private Long id;
    private String content;
    private Long recordId;
    private String problemTitle;
    private List<String> categories;
    @JsonFormat(pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static CoreIdeaDTO fromEntity(RecordCoreIdea entity) {
        return CoreIdeaDTO.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .recordId(entity.getRecord().getId())
                .problemTitle(entity.getRecord().getProblem().getTitle())
                .categories(
                        entity.getRecord().getRecordCategories().stream()
                                .map(rc -> rc.getCategory().getName())
                                .toList()
                )
                .createdAt(entity.getRecord().getCreatedAt())
                .build();
    }

}
