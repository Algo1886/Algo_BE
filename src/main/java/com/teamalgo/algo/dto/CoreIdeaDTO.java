package com.teamalgo.algo.dto;

import com.teamalgo.algo.domain.record.RecordCoreIdea;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreIdeaDTO {
    private Long id;
    private String content;
    private Long recordId;
    private String authorName;

    public static CoreIdeaDTO fromEntity(RecordCoreIdea entity) {
        return CoreIdeaDTO.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .recordId(entity.getRecord().getId())
                .authorName(entity.getRecord().getUser().getUsername())
                .build();
    }
}
