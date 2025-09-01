package com.teamalgo.algo.dto.request;

import com.teamalgo.algo.dto.RecordCodeDTO;
import com.teamalgo.algo.dto.RecordCoreIdeaDTO;
import com.teamalgo.algo.dto.RecordLinkDTO;
import com.teamalgo.algo.dto.RecordStepDTO;
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

    @NotBlank(message = "Problem title cannot be blank")
    private String title;

    @NotEmpty(message = "Categories cannot be empty")
    private List<String> categories;

    @NotNull(message = "Status cannot be null")
    @Pattern(regexp = "success|fail", message = "Status must be 'success' or 'fail'")
    private String status;

    @Min(value = 1, message = "Difficulty must be between 1 and 5")
    @Max(value = 5, message = "Difficulty must be between 1 and 5")
    private int difficulty;

    @Size(max = 2000, message = "Detail should not exceed 2000 characters")
    private String detail;

    private List<RecordCodeDTO> codes;
    private List<RecordStepDTO> steps;
    private List<RecordCoreIdeaDTO> ideas;
    private List<RecordLinkDTO> links;

    private boolean isDraft;
    private boolean isPublished;
}
