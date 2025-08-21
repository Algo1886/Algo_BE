package com.teamalgo.algo.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.problem.Problem;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.repository.ProblemRepository;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordCreateRequest {

    @NotBlank(message = "Problem URL cannot be blank")
    private String problemUrl;

    @NotNull(message = "Problem source cannot be null")
    private String source;

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

    @Size(max = 500, message = "Detail should not exceed 500 characters")
    private String detail;      // 풀이 상세 설명

    private List<RecordCodeDTO> codes;
    private List<RecordStepDTO> steps;
    private List<RecordCoreIdeaDTO> ideas;
    private List<RecordLinkDTO> links;

    private boolean isDraft;
    private boolean isPublished;

    // 문제 url로 기존 문제 찾거나 새로 생성
    public Problem mapToProblem(ProblemRepository problemRepository) {
        return problemRepository.findByUrl(problemUrl)
                .orElseGet(() -> {
                    Problem newProblem = Problem.builder()
                            .url(problemUrl)
                            .title(title)
                            .source(source)
                            .externalId(generateExternalId())
                            .build();
                    return problemRepository.save(newProblem); // 새 문제를 DB에 저장
                });
    }

    // Problem 엔터티에서 externalId 필드에 할당
    private String generateExternalId() {
        return UUID.randomUUID().toString(); // 고유 외부 ID 생성
    }

    public Record toEntity(User user, Problem problem) {
        // Record 엔터티를 생성하여 반환
        return Record.builder()
                .user(user)
                .problem(problem)
                .status(status)
                .difficulty(difficulty)
                .detail(detail)
                .isDraft(isDraft)
                .isPublished(isPublished)
                .build();
    }
}
