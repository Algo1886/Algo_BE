package com.teamalgo.algo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.teamalgo.algo.domain.record.RecordCode;
import com.teamalgo.algo.domain.record.Record;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordCodeDTO {
    private Long id; // Create 시 null, Update/Response 시 존재

    @NotNull(message = "Language cannot be null")
    private String language;

    @NotNull(message = "Code cannot be null")
    private String code;

    @NotNull(message = "Verdict cannot be null")
    @Pattern(regexp = "pass|fail", message = "Verdict must be 'pass' or 'fail'")
    private String verdict;

    @Min(value = 0, message = "Snippet order must be 0 or greater")
    private int snippetOrder;

    public RecordCode toEntity(Record record) {
        return RecordCode.builder()
                .id(id) // null이면 새로 생성됨
                .record(record)
                .language(language)
                .code(code)
                .verdict(verdict)
                .snippetOrder(snippetOrder)
                .build();
    }

    public static RecordCodeDTO fromEntity(RecordCode entity) {
        return RecordCodeDTO.builder()
                .id(entity.getId())
                .language(entity.getLanguage())
                .code(entity.getCode())
                .verdict(entity.getVerdict())
                .snippetOrder(entity.getSnippetOrder())
                .build();
    }
}
