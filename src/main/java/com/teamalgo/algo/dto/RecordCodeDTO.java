package com.teamalgo.algo.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Code cannot be blank")
    private String code;

    @NotBlank(message = "Language cannot be blank")
    private String language;

    @NotBlank(message = "Verdict cannot be blank")
    @Pattern(regexp = "pass|fail", message = "Verdict must be 'pass' or 'fail'")
    private String verdict;

    @Min(value = 0, message = "Snippet order must be 0 or greater")
    private int snippetOrder;

    public RecordCode toEntity(Record record) {
        return RecordCode.builder()
                .record(record)
                .language(language)
                .code(code)
                .verdict(verdict)
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
