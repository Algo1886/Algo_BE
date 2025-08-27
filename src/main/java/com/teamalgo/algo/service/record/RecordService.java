package com.teamalgo.algo.service.record;

import com.teamalgo.algo.domain.problem.Problem;
import com.teamalgo.algo.domain.record.*;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.domain.category.*;
import com.teamalgo.algo.dto.*;
import com.teamalgo.algo.dto.request.RecordCreateRequest;
import com.teamalgo.algo.dto.request.RecordSearchRequest;
import com.teamalgo.algo.dto.request.RecordUpdateRequest;
import com.teamalgo.algo.dto.response.RecordListResponse;
import com.teamalgo.algo.dto.response.RecordResponse;
import com.teamalgo.algo.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final ProblemRepository problemRepository;
    private final CategoryRepository categoryRepository;
    private final BookmarkService bookmarkService;

    // 레코드 생성
    @Transactional
    public com.teamalgo.algo.domain.record.Record createRecord(User user, RecordCreateRequest req) {
        Problem problem = problemRepository.findByUrl(req.getProblemUrl())
                .orElseGet(() -> problemRepository.save(
                        Problem.builder()
                                .url(req.getProblemUrl())
                                .title(req.getTitle())
                                .source(req.getSource())
                                .externalId(UUID.randomUUID().toString())
                                .build()
                ));

        com.teamalgo.algo.domain.record.Record record = com.teamalgo.algo.domain.record.Record.builder()
                .user(user)
                .problem(problem)
                .status(req.getStatus())
                .difficulty(req.getDifficulty())
                .detail(req.getDetail())
                .isDraft(req.isDraft())
                .isPublished(req.isPublished())
                .build();

        // Codes (항상 서버에서 snippetOrder 재지정)
        if (req.getCodes() != null) {
            AtomicInteger order = new AtomicInteger(0);
            for (RecordCodeDTO dto : req.getCodes()) {
                RecordCode entity = dto.toEntity(record);
                entity.update(dto.getLanguage(), dto.getCode(), dto.getVerdict(), order.getAndIncrement());
                record.getCodes().add(entity);
            }
        }

        // Steps (항상 서버에서 stepOrder 재지정)
        if (req.getSteps() != null) {
            AtomicInteger order = new AtomicInteger(0);
            for (RecordStepDTO dto : req.getSteps()) {
                RecordStep entity = dto.toEntity(record);
                entity.update(order.getAndIncrement(), dto.getText());
                record.getSteps().add(entity);
            }
        }

        if (req.getIdeas() != null) {
            record.getIdeas().addAll(req.getIdeas().stream()
                    .map(dto -> dto.toEntity(record)).toList());
        }
        if (req.getLinks() != null) {
            record.getLinks().addAll(req.getLinks().stream()
                    .map(dto -> dto.toEntity(record)).toList());
        }

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

        return recordRepository.save(record);
    }

    // 조회
    public com.teamalgo.algo.domain.record.Record getRecordById(Long id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record not found: " + id));
    }

    public Page<com.teamalgo.algo.domain.record.Record> searchRecords(RecordSearchRequest req) {
        Sort sort = (req.getSort() == RecordSearchRequest.SortType.LATEST)
                ? Sort.by(Sort.Direction.DESC, "createdAt")
                : Sort.by(Sort.Direction.DESC, "id");

        Pageable pageable = PageRequest.of(req.getPageIndex(), req.getSize(), sort);

        return recordRepository.findAll(pageable);
    }

    // 레코드 수정 (블로그 전체 교체 방식)
    @Transactional
    public com.teamalgo.algo.domain.record.Record updateRecord(
            Long id,
            RecordUpdateRequest req,
            User user
    ) {
        com.teamalgo.algo.domain.record.Record record = getRecordById(id);

        if (!record.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        // --- 단일 필드 갱신 ---
        record.updateDetail(req.getDetail());
        if (req.getIsDraft() != null) record.updateDraft(req.getIsDraft());
        if (req.getIsPublished() != null) record.updatePublished(req.getIsPublished());

        // --- Codes 전체 교체 ---
        if (req.getCodes() != null) {
            record.getCodes().clear();
            recordRepository.flush(); // ✅ DELETE 먼저 DB 반영
            AtomicInteger order = new AtomicInteger(0);
            for (RecordCodeDTO dto : req.getCodes()) {
                RecordCode entity = dto.toEntity(record);
                entity.update(dto.getLanguage(), dto.getCode(), dto.getVerdict(), order.getAndIncrement());
                record.getCodes().add(entity);
            }
        }

        // --- Steps 전체 교체 ---
        if (req.getSteps() != null) {
            record.getSteps().clear();
            recordRepository.flush(); // ✅ DELETE 먼저 DB 반영
            AtomicInteger order = new AtomicInteger(0);
            for (RecordStepDTO dto : req.getSteps()) {
                RecordStep entity = dto.toEntity(record);
                entity.update(order.getAndIncrement(), dto.getText());
                record.getSteps().add(entity);
            }
        }

        // --- Ideas 전체 교체 ---
        if (req.getIdeas() != null) {
            record.getIdeas().clear();
            recordRepository.flush(); // ✅ DELETE 먼저 DB 반영
            for (RecordCoreIdeaDTO dto : req.getIdeas()) {
                record.getIdeas().add(dto.toEntity(record));
            }
        }

        // --- Links 전체 교체 ---
        if (req.getLinks() != null) {
            record.getLinks().clear();
            recordRepository.flush(); // ✅ DELETE 먼저 DB 반영
            for (RecordLinkDTO dto : req.getLinks()) {
                record.getLinks().add(dto.toEntity(record));
            }
        }

        // --- Categories 전체 교체 ---
        if (req.getCategories() != null) {
            record.getRecordCategories().clear();
            recordRepository.flush(); // ✅ DELETE 먼저 DB 반영
            for (String catName : req.getCategories()) {
                Category category = categoryRepository.findByName(catName)
                        .orElseGet(() -> categoryRepository.save(
                                Category.builder()
                                        .name(catName)
                                        .slug(catName.toLowerCase().replace(" ", "-"))
                                        .build()
                        ));
                record.getRecordCategories().add(
                        RecordCategory.builder()
                                .record(record)
                                .category(category)
                                .build()
                );
            }
        }

        return record;
    }


    // 레코드 삭제
    @Transactional
    public void deleteRecord(Long id, User user) {
        com.teamalgo.algo.domain.record.Record record = getRecordById(id);

        if (!record.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
        recordRepository.delete(record);
    }

    // Response DTO 변환
    public RecordResponse.Data createRecordResponse(com.teamalgo.algo.domain.record.Record record, User user) {
        return RecordResponse.Data.builder()
                .id(record.getId())
                .problem(mapProblem(record.getProblem()))
                .categories(mapCategories(record))
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

    public RecordListResponse.Data createRecordListResponse(Page<com.teamalgo.algo.domain.record.Record> records) {
        List<RecordDTO> recordDTOs = records.stream()
                .map(RecordDTO::from)
                .toList();

        return RecordListResponse.Data.builder()
                .records(recordDTOs)
                .page(records.getNumber() + 1)
                .size(records.getSize())
                .totalElements(records.getTotalElements())
                .totalPages(records.getTotalPages())
                .first(records.isFirst())
                .last(records.isLast())
                .build();
    }

    // --- 매핑 헬퍼 ---
    private ProblemDTO mapProblem(Problem problem) {
        return ProblemDTO.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .url(problem.getUrl())
                .source(problem.getSource())
                .externalId(problem.getExternalId())
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
