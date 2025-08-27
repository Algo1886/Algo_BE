package com.teamalgo.algo.dto.response;

import com.teamalgo.algo.dto.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class RecordResponse {

    @Getter
    @Setter
    @Builder
    public static class Data {
        private Long id;

        private ProblemDTO problem;
        private List<String> categories;

        private String status;
        private int difficulty;
        private String detail;

        private List<RecordCodeDTO> codes;
        private List<RecordStepDTO> steps;
        private List<RecordCoreIdeaDTO> ideas;
        private List<RecordLinkDTO> links;

        private AuthorDTO author;

        private Boolean isDraft;
        private Boolean isPublished;
        private Boolean isBookmarked;
        private Boolean isOwner;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
