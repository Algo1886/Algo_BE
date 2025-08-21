package com.teamalgo.algo.dto;

import com.teamalgo.algo.domain.record.RecordCoreIdea;  // RecordCoreIdea 엔티티 import
import com.teamalgo.algo.domain.record.Record;     // 올바른 Record 엔티티 import
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class RecordCoreIdeaDTO {

    @NotNull(message = "Content cannot be null")
    private String content;


    public RecordCoreIdea toEntity(Record record) {
        return RecordCoreIdea.builder()
                .record(record)
                .content(content)
                .build();
    }
}
