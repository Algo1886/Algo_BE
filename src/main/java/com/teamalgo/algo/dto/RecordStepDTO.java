package com.teamalgo.algo.dto;

import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.record.RecordStep;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordStepDTO {

    private Long id;       // update/삭제 시 필요
    private int stepOrder; // 단계 순서
    private String text;   // 단계 설명

    public RecordStep toEntity(Record record) {
        return RecordStep.builder()
                .record(record)
                .text(text)
                .build();
    }

    public static RecordStepDTO fromEntity(RecordStep entity) {
        return RecordStepDTO.builder()
                .id(entity.getId())
                .stepOrder(entity.getStepOrder())
                .text(entity.getText())
                .build();
    }
}
