package com.teamalgo.algo.dto;

import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.record.RecordCoreIdea;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordCoreIdeaDTO {

    private Long id;
    private String content;

    public RecordCoreIdea toEntity(Record record) {
        return RecordCoreIdea.builder()
                .id(id)
                .record(record)
                .content(content)
                .build();
    }

    public static RecordCoreIdeaDTO fromEntity(RecordCoreIdea entity) {
        return RecordCoreIdeaDTO.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .build();
    }
}
