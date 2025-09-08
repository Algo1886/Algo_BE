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

    @NotEmpty(message = "Categories cannot be empty")
    private List<String> categories;

    @NotNull(message = "Status cannot be null")
    @Pattern(regexp = "success|fail", message = "Status must be 'success' or 'fail'")
    private String status;

    @Min(value = 1, message = "Difficulty must be between 1 and 5")
    @Max(value = 5, message = "Difficulty must be between 1 and 5")
    private int difficulty;

    @NotBlank(message = "Detail cannot be blank")
    @Size(max = 2000, message = "Detail should not exceed 2000 characters")
    private String detail;

    @NotNull(message = "At least one code is required")
    @Size(min = 1, message = "At least one code is required")
    private List<@Valid RecordCodeDTO> codes;

    @NotNull(message = "At least one step is required")
    @Size(min = 1, message = "At least one step is required")
    private List<@Valid RecordStepDTO> steps;

    private List<RecordCoreIdeaDTO> ideas;
    private List<RecordLinkDTO> links;

    @JsonProperty("draft")
    private boolean isDraft;

    @JsonProperty("published")
    private boolean isPublished;
}
