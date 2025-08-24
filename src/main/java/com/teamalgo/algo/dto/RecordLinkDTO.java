package com.teamalgo.algo.dto;

import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.record.RecordLink;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordLinkDTO {

    private Long id;   // update/삭제 시 필요
    private String url;

    public RecordLink toEntity(Record record) {
        return RecordLink.builder()
                .id(id)
                .record(record)
                .url(url)
                .build();
    }

    public static RecordLinkDTO fromEntity(RecordLink entity) {
        return RecordLinkDTO.builder()
                .id(entity.getId())
                .url(entity.getUrl())
                .build();
    }
}
