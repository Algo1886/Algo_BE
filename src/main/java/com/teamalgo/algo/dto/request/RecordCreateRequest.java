package com.teamalgo.algo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.teamalgo.algo.dto.RecordCodeDTO;
import com.teamalgo.algo.dto.RecordCoreIdeaDTO;
import com.teamalgo.algo.dto.RecordLinkDTO;
import com.teamalgo.algo.dto.RecordStepDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordCreateRequest {

    @NotBlank(message = "Problem URL cannot be blank")
    private String problemUrl;

    @Size(max = 200, message = "Title should not exceed 200 characters")
    private String customTitle;

    private List<Long> categoryIds;
    private String status;
    private Integer difficulty;
    private String detail;

    @Size(max = 3, message = "코드는 최대 3개까지만 등록 가능합니다.")
    private List<@Valid RecordCodeDTO> codes;
    @Size(max = 10, message = "풀이 과정은 최대 10개까지만 등록 가능합니다.")
    private List<@Valid RecordStepDTO> steps;

    private List<RecordCoreIdeaDTO> ideas;
    private List<RecordLinkDTO> links;

    @JsonProperty("draft")
    private boolean isDraft;

    @JsonProperty("published")
    private boolean isPublished;
}
