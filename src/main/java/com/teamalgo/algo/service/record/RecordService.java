package com.teamalgo.algo.service.record;

import com.teamalgo.algo.domain.problem.Problem;
import com.teamalgo.algo.domain.record.RecordCode;
import com.teamalgo.algo.domain.record.RecordStep;
import com.teamalgo.algo.domain.category.RecordCategory;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.domain.category.Category;
import com.teamalgo.algo.dto.RecordCodeDTO;
import com.teamalgo.algo.dto.RecordCoreIdeaDTO;
import com.teamalgo.algo.dto.RecordDTO;
import com.teamalgo.algo.dto.RecordLinkDTO;
import com.teamalgo.algo.dto.RecordStepDTO;
import com.teamalgo.algo.dto.AuthorDTO;
import com.teamalgo.algo.dto.request.RecordCreateRequest;
import com.teamalgo.algo.dto.request.RecordSearchRequest;
import com.teamalgo.algo.dto.request.RecordUpdateRequest;
import com.teamalgo.algo.dto.response.RecordListResponse;
import com.teamalgo.algo.dto.response.RecordResponse;
import com.teamalgo.algo.dto.response.ProblemPreviewResponse;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.repository.RecordRepository;
import com.teamalgo.algo.repository.ProblemRepository;
import com.teamalgo.algo.repository.CategoryRepository;
import com.teamalgo.algo.service.problem.ProblemService;
import com.teamalgo.algo.service.stats.StatsService;
import com.teamalgo.algo.global.common.util.ProblemSourceDetector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final ProblemRepository problemRepository;
    private final CategoryRepository categoryRepository;
    private final BookmarkService bookmarkService;
    private final StatsService statsService;
    private final ProblemService problemService;

    @PersistenceContext
    private EntityManager entityManager;

    // 레코드 생성
    @Transactional
    public com.teamalgo.algo.domain.record.Record createRecord(User user, RecordCreateRequest req) {
        Problem problem = problemRepository.findByUrl(req.getProblemUrl())
                .orElseGet(() -> {
                    ProblemPreviewResponse preview = problemService.fetchProblemInfo(req.getProblemUrl());

                    String finalTitle = (preview.getTitle() != null && !preview.getTitle().isBlank())
                            ? preview.getTitle()
                            : req.getCustomTitle();

                    if (finalTitle == null || finalTitle.isBlank()) {
                        throw new CustomException(ErrorCode.INVALID_REQUEST);
                    }

                    return problemRepository.save(
                            Problem.builder()
                                    .url(preview.getUrl())
                                    .source(preview.getSource())
                                    .title(finalTitle)
                                    .numericId(ProblemSourceDetector.extractNumericId(preview.getUrl(), preview.getSource()))
                                    .slugId(ProblemSourceDetector.extractSlugId(preview.getUrl(), preview.getSource()))
                                    .build()
                    );
                });

        com.teamalgo.algo.domain.record.Record record = com.teamalgo.algo.domain.record.Record.builder()
                .user(user)
                .problem(problem)
                .status(req.getStatus())
                .difficulty(req.getDifficulty())
                .detail(req.getDetail())
                .isDraft(req.isDraft())
                .isPublished(req.isPublished())
                .customTitle(req.getCustomTitle())
                .build();

        // Codes
        if (req.getCodes() != null) {
            AtomicInteger order = new AtomicInteger(0);
            for (RecordCodeDTO dto : req.getCodes()) {
                RecordCode entity = dto.toEntity(record);
                entity.update(dto.getLanguage(), dto.getCode(), dto.getVerdict(), order.getAndIncrement());
                record.getCodes().add(entity);
            }
        }

        // Steps
        if (req.getSteps() != null) {
            AtomicInteger order = new AtomicInteger(0);
            for (RecordStepDTO dto : req.getSteps()) {
                RecordStep entity = dto.toEntity(record);
                entity.update(order.getAndIncrement(), dto.getText());
                record.getSteps().add(entity);
            }
        }

        // Ideas
        if (req.getIdeas() != null) {
            record.getIdeas().addAll(req.getIdeas().stream()
                    .map(dto -> dto.toEntity(record))
                    .toList());
        }

        // Links
        if (req.getLinks() != null) {
            record.getLinks().addAll(req.getLinks().stream()
                    .map(dto -> dto.toEntity(record))
                    .toList());
        }

        // Categories
        if (req.getCategories() != null) {
            List<RecordCategory> recordCategories = new HashSet<>(req.getCategories()).stream()
                    .map(catName -> {
                        Category category = categoryRepository.findByName(catName)
                                .orElseGet(() -> categoryRepository.save(
                                        Category.builder()
                                                .name(catName)
                                                .slug(catName.toLowerCase().replace(" ", "-"))
                                                .build()
                                ));
                        return RecordCategory.builder()
                                .record(record)
                                .category(category)
                                .build();
                    })
                    .toList();

            record.getRecordCategories().addAll(recordCategories);
        }

        boolean isSuccess = record.getStatus().equals("success");

        // 임시 저장 아닐때만 통계 반영
        if (!record.isDraft()) {
            statsService.increaseStats(user, isSuccess);
        }

        return recordRepository.save(record);
    }

    // 레코드 단건 조회
    // 발행글은 모두 접근 가능, 비공개/임시저장글은 작성자 본인만
    public com.teamalgo.algo.domain.record.Record getRecordById(Long id, User user) {
        return recordRepository.findById(id)
                .filter(r -> {
                    if (r.isDraft()) return r.getUser().getId().equals(user.getId()); // draft → 작성자만
                    if (!r.isPublished()) return r.getUser().getId().equals(user.getId()); // private → 작성자만
                    return true;
                })
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));
    }

    //  레코드 목록 조회
    public Page<com.teamalgo.algo.domain.record.Record> searchRecords(
            RecordSearchRequest req,
            boolean isAuthenticated
    ) {
        if (req.getSort() == RecordSearchRequest.SortType.POPULAR) {
            LocalDateTime start = req.getStartDate() != null ? req.getStartDate().atStartOfDay() : null;
            LocalDateTime end = req.getEndDate() != null ? req.getEndDate().plusDays(1).atStartOfDay().minusNanos(1) : null;

            if(isAuthenticated) {
            return recordRepository.findPopularWithFilters(
                    (req.getSearch() != null && !req.getSearch().isBlank()) ? "%" + req.getSearch() + "%" : null,
                    (req.getAuthor() != null && !req.getAuthor().isBlank()) ? req.getAuthor() : null,
                    (req.getCategory() != null && !req.getCategory().isBlank()) ? req.getCategory() : null,
                    start,
                    end,
                    PageRequest.of(req.getPageIndex(), req.getSize())
            );
        } else {
                return recordRepository.findPopularWithFilters(
                        null, null, null, start, end,
                        PageRequest.of(req.getPageIndex(), req.getSize())
                );
            }
        }

        Sort sort = (req.getSort() == RecordSearchRequest.SortType.LATEST)
                ? Sort.by(Sort.Direction.DESC, "createdAt")
                : Sort.by(Sort.Direction.DESC, "id");

        Pageable pageable = PageRequest.of(req.getPageIndex(), req.getSize(), sort);
        Specification<com.teamalgo.algo.domain.record.Record> spec = Specification.where(
                (root, query, cb) -> cb.and(
                        cb.isFalse(root.get("isDraft")),
                        cb.isTrue(root.get("isPublished"))
                )
        );

        if (isAuthenticated) {
            if (req.getSearch() != null && !req.getSearch().isBlank()) {
                String keyword = "%" + req.getSearch() + "%";
                spec = spec.and((root, query, cb) -> {
                    Join<com.teamalgo.algo.domain.record.Record, Problem> problem = root.join("problem");
                    return cb.or(
                            cb.like(root.get("customTitle"), keyword),
                            cb.like(problem.get("title"), keyword)
                    );
                });
            }

            if (req.getAuthor() != null && !req.getAuthor().isBlank()) {
                spec = spec.and((root, query, cb) -> {
                    Join<com.teamalgo.algo.domain.record.Record, User> user = root.join("user");
                    return cb.equal(user.get("username"), req.getAuthor());
                });
            }

            if (req.getCategory() != null && !req.getCategory().isBlank()) {
                spec = spec.and((root, query, cb) -> {
                    Join<com.teamalgo.algo.domain.record.Record, RecordCategory> rc = root.join("recordCategories");
                    Join<RecordCategory, Category> category = rc.join("category");
                    return cb.equal(category.get("name"), req.getCategory());
                });
            }

            if (req.getStartDate() != null && req.getEndDate() != null) {
                LocalDateTime start = req.getStartDate().atStartOfDay();
                LocalDateTime end = req.getEndDate().plusDays(1).atStartOfDay();
                spec = spec.and((root, query, cb) ->
                        cb.between(root.get("createdAt"), start, end.minusNanos(1)));
            }
        }

        return recordRepository.findAll(spec, pageable);
    }

    //  내 레코드 목록
    public Page<com.teamalgo.algo.domain.record.Record> getRecordsByUser(User user, Pageable pageable) {
        return recordRepository.findByUserIdAndIsDraftFalse(user.getId(), pageable);
    }

    // Draft 목록
    public Page<com.teamalgo.algo.domain.record.Record> getDraftsByUser(User user, Pageable pageable) {
        return recordRepository.findByUserIdAndIsDraftTrue(user.getId(), pageable);
    }

    // 레코드 수정
    @Transactional
    public com.teamalgo.algo.domain.record.Record updateRecord(Long id, RecordUpdateRequest req, User user) {
        com.teamalgo.algo.domain.record.Record record = recordRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }


        boolean prevDraft = record.isDraft();

        // Custom title
        if (req.getCustomTitle() != null) {
            record.updateCustomTitle(req.getCustomTitle());
        }

        // Detail
        if (req.getDetail() != null) {
            record.updateDetail(req.getDetail());
        }

        // Draft
        if (req.getIsDraft() != null) {
            record.updateDraft(req.getIsDraft());
        }

        // Published
        if (req.getIsPublished() != null) {
            record.updatePublished(req.getIsPublished());
        }

        // Codes
        if (req.getCodes() != null) {
            if (req.getCodes().isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_REQUEST);
            }
            record.getCodes().clear();
            recordRepository.flush();
            AtomicInteger order = new AtomicInteger(0);
            for (RecordCodeDTO dto : req.getCodes()) {
                RecordCode entity = dto.toEntity(record);
                entity.update(dto.getLanguage(), dto.getCode(), dto.getVerdict(), order.getAndIncrement());
                record.getCodes().add(entity);
            }
        }

        // Steps
        if (req.getSteps() != null) {
            if (req.getSteps().isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_REQUEST);
            }
            record.getSteps().clear();
            recordRepository.flush();
            AtomicInteger order = new AtomicInteger(0);
            for (RecordStepDTO dto : req.getSteps()) {
                RecordStep entity = dto.toEntity(record);
                entity.update(order.getAndIncrement(), dto.getText());
                record.getSteps().add(entity);
            }
        }

        // Ideas
        if (req.getIdeas() != null) {
            record.getIdeas().clear();
            recordRepository.flush();
            for (RecordCoreIdeaDTO dto : req.getIdeas()) {
                record.getIdeas().add(dto.toEntity(record));
            }
        }

        // Links
        if (req.getLinks() != null) {
            record.getLinks().clear();
            recordRepository.flush();
            for (RecordLinkDTO dto : req.getLinks()) {
                record.getLinks().add(dto.toEntity(record));
            }
        }

        // Categories
        if (req.getCategories() != null) {
            record.getRecordCategories().clear();
            recordRepository.flush();
            for (String catName : req.getCategories()) {
                Category category = categoryRepository.findByName(catName)
                        .orElseGet(() -> categoryRepository.save(
                                Category.builder()
                                        .name(catName)
                                        .slug(catName.toLowerCase().replace(" ", "-"))
                                        .build()
                        ));
                record.getRecordCategories().add(
                        com.teamalgo.algo.domain.category.RecordCategory.builder()
                                .record(record)
                                .category(category)
                                .build()
                );
            }
        }

        // 임시저장 -> 발행으로 전환된 경우 통계 반영
        if (prevDraft && !record.isDraft()) {
            boolean isSuccess = record.getStatus().equals("success");
            statsService.increaseStats(user, isSuccess);
        }

        return record;
    }


    // 레코드 삭제
    @Transactional
    public void deleteRecord(Long id, User user) {
        com.teamalgo.algo.domain.record.Record record = recordRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        recordRepository.delete(record);

        if (!record.isDraft()) {
            // 통계 반영
            LocalDate date = record.getCreatedAt().toLocalDate();
            boolean isSuccess = record.getStatus().equals("success");
            statsService.decreaseStats(user, date, isSuccess);
        }
    }

    // 단건 응답 변환
    public RecordResponse.Data createRecordResponse(com.teamalgo.algo.domain.record.Record record, User user) {
        String finalTitle = (record.getCustomTitle() != null && !record.getCustomTitle().isBlank())
                ? record.getCustomTitle()
                : record.getProblem().getTitle();

        return RecordResponse.Data.builder()
                .id(record.getId())
                .title(finalTitle)
                .problemUrl(record.getProblem().getUrl())
                .categories(mapCategories(record))
                .source(record.getProblem().getSource())
                .status(record.getStatus())
                .difficulty(record.getDifficulty() != null ? record.getDifficulty() : 0)
                .detail(record.getDetail())
                .codes(record.getCodes().stream().map(RecordCodeDTO::fromEntity).toList())
                .steps(record.getSteps().stream().map(RecordStepDTO::fromEntity).toList())
                .ideas(record.getIdeas().stream().map(RecordCoreIdeaDTO::fromEntity).toList())
                .links(record.getLinks().stream().map(RecordLinkDTO::fromEntity).toList())
                .author(mapAuthor(record.getUser()))
                .isDraft(record.isDraft())
                .isPublished(record.isPublished())
                .isBookmarked(bookmarkService.isBookmarked(user, record))
                .isOwner(record.getUser().getId().equals(user.getId()))
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    // 목록 응답 변환
    public RecordListResponse createRecordListResponse(Page<com.teamalgo.algo.domain.record.Record> records) {
        List<RecordDTO> recordDTOs = records.stream()
                .map(RecordDTO::from)
                .toList();

        return RecordListResponse.builder()
                .records(recordDTOs)
                .page(records.getNumber() + 1)
                .size(records.getSize())
                .totalElements(records.getTotalElements())
                .totalPages(records.getTotalPages())
                .first(records.isFirst())
                .last(records.isLast())
                .build();
    }

    private AuthorDTO mapAuthor(User user) {
        return AuthorDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    private List<String> mapCategories(com.teamalgo.algo.domain.record.Record record) {
        return record.getRecordCategories().stream()
                .map(rc -> rc.getCategory().getName())
                .toList();
    }
}
