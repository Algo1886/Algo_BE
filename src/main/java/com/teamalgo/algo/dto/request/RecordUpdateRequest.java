package com.teamalgo.algo.dto.request;

import com.teamalgo.algo.dto.RecordCodeDTO;
import com.teamalgo.algo.dto.RecordCoreIdeaDTO;
import com.teamalgo.algo.dto.RecordLinkDTO;
import com.teamalgo.algo.dto.RecordStepDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RecordUpdateRequest {

    @NotNull(message = "Record ID cannot be null")
    private Long id;

    @Size(max = 500, message = "Detail should not exceed 500 characters")
    private String detail;

    private List<RecordCodeDTO> codes;
    private List<RecordStepDTO> steps;
    private List<RecordCoreIdeaDTO> ideas;
    private List<RecordLinkDTO> links;

    private Boolean isDraft;     // null 가능 → patch 방식
    private Boolean isPublished; // null 가능 → patch 방식
}
