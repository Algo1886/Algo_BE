package com.teamalgo.algo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.teamalgo.algo.domain.record.RecordStep;
import com.teamalgo.algo.domain.record.Record;
@Getter
@Setter
@Builder
public class RecordStepDTO {

    private int stepOrder;  // 단계 순서
    private String text;    // 풀이 과정 설명

    // RecordStep 엔티티로 변환하는 메서드
    public RecordStep toEntity(Record record) {
        return RecordStep.builder()
                .record(record)
                .stepOrder(stepOrder)
                .text(text)
                .build();
    }
}
