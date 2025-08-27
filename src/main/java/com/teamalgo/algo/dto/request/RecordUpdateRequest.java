package com.teamalgo.algo.dto.request;

import com.teamalgo.algo.dto.RecordCodeDTO;
import com.teamalgo.algo.dto.RecordCoreIdeaDTO;
import com.teamalgo.algo.dto.RecordLinkDTO;
import com.teamalgo.algo.dto.RecordStepDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@Schema(description = "레코드 수정 요청 DTO (전체 교체)")
public class RecordUpdateRequest {

    @Size(max = 500, message = "Detail should not exceed 500 characters")
    @Schema(description = "상세 설명", example = "알고리즘 최적화 방법을 추가로 작성했습니다.")
    private String detail;

    @Schema(description = "코드 스니펫 목록")
    private List<RecordCodeDTO> codes;

    @Schema(description = "풀이 단계 목록")
    private List<RecordStepDTO> steps;

    @Schema(description = "핵심 아이디어 목록")
    private List<RecordCoreIdeaDTO> ideas;

    @Schema(description = "관련 링크 목록")
    private List<RecordLinkDTO> links;

    @Schema(description = "카테고리 목록", example = "[\"DP\", \"Graph\"]")
    private List<String> categories;

    @NotNull
    @Schema(description = "임시 저장 여부", example = "true")
    private Boolean isDraft;

    @NotNull
    @Schema(description = "공개 여부", example = "false")
    private Boolean isPublished;
}
