package com.teamalgo.algo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.teamalgo.algo.domain.record.RecordCode;
import com.teamalgo.algo.domain.record.Record;

@Getter
@Setter
@Builder
public class RecordCodeDTO {

    @NotNull(message = "Language cannot be null")
    private String language;    // 코드 언어 (드롭다운에서 선택)

    @NotNull(message = "Code cannot be null")
    private String code;

    @NotNull(message = "Verdict cannot be null")
    @Pattern(regexp = "pass|fail", message = "Verdict must be 'pass' or 'fail'")
    private String verdict;

    private int snippetOrder;

    public RecordCode toEntity(Record record) {
        return RecordCode.builder()
                .record(record)
                .language(language)
                .code(code)
                .verdict(verdict)
                .snippetOrder(snippetOrder)
                .build();
    }
}
