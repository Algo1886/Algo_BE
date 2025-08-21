package com.teamalgo.algo.dto;

import com.teamalgo.algo.domain.record.RecordLink;
import com.teamalgo.algo.domain.record.Record;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RecordLinkDTO {

    private String url;

    public RecordLink toEntity(Record record) {
        return RecordLink.builder()
                .record(record)
                .url(url)
                .build();
    }
}
