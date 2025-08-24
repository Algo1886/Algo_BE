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
import java.util.function.Function;
import java.util.stream.Collectors;

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
        // 1. 문제 찾거나 없으면 새로 생성
        Problem problem = problemRepository.findByUrl(req.getProblemUrl())
                .orElseGet(() -> problemRepository.save(
                        Problem.builder()
                                .url(req.getProblemUrl())
                                .title(req.getTitle())
                                .source(req.getSource())
                                .externalId(UUID.randomUUID().toString())
                                .build()
                ));

        // 2. Record 생성
        com.teamalgo.algo.domain.record.Record record = com.teamalgo.algo.domain.record.Record.builder()
                .user(user)
                .problem(problem)
                .status(req.getStatus())
                .difficulty(req.getDifficulty())
                .detail(req.getDetail())
                .isDraft(req.isDraft())
                .isPublished(req.isPublished())
                .build();

        // 3. Codes, Steps, Ideas, Links 저장
        if (req.getCodes() != null) {
            record.getCodes().addAll(req.getCodes().stream()
                    .map(dto -> dto.toEntity(record)).toList());
        }
        if (req.getSteps() != null) {
            record.getSteps().addAll(req.getSteps().stream()
                    .map(dto -> dto.toEntity(record)).toList());
        }
        if (req.getIdeas() != null) {
            record.getIdeas().addAll(req.getIdeas().stream()
                    .map(dto -> dto.toEntity(record)).toList());
        }
        if (req.getLinks() != null) {
            record.getLinks().addAll(req.getLinks().stream()
                    .map(dto -> dto.toEntity(record)).toList());
        }

        // 카테고리 저장
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

        // 5. DB 저장
        return recordRepository.save(record);
    }

    // 조회
    public com.teamalgo.algo.domain.record.Record getRecordById(Long id) {
        return recordRepository.findWithDetailsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record not found: " + id));
    }

    public Page<com.teamalgo.algo.domain.record.Record> searchRecords(RecordSearchRequest req) {
        Sort sort = (req.getSort() == RecordSearchRequest.SortType.LATEST)
                ? Sort.by(Sort.Direction.DESC, "createdAt")
                : Sort.by(Sort.Direction.DESC, "id"); // POPULAR은 추후 구현

        Pageable pageable = PageRequest.of(req.getPageIndex(), req.getSize(), sort);

        return recordRepository.findAll(pageable);
    }

    // 레코드 수정
    @Transactional
    public com.teamalgo.algo.domain.record.Record patchRecord(Long id, RecordUpdateRequest req, User user) {
        com.teamalgo.algo.domain.record.Record record = getRecordById(id);

        if (!record.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        record.applyPatch(req);


        if (req.getCodes() != null) syncCodes(record, req.getCodes());
        if (req.getSteps() != null) syncSteps(record, req.getSteps());
        if (req.getIdeas() != null) syncIdeas(record, req.getIdeas());
        if (req.getLinks() != null) syncLinks(record, req.getLinks());

        return record;
    }

    // 레코드 삭제
    @Transactional
    public void deleteRecord(Long id, User user) {
        com.teamalgo.algo.domain.record.Record record = getRecordById(id);

        // 소유자 검증
        if (!record.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
        recordRepository.delete(record);
    }

    // Response dto 변환
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
                .page(records.getNumber() + 1) // Page는 0-based라 +1
                .size(records.getSize())
                .totalElements(records.getTotalElements())
                .totalPages(records.getTotalPages())
                .first(records.isFirst())
                .last(records.isLast())
                .build();
    }


    // 레코드의 자식 엔터티 (코드, 스텝, 핵심 아이디어, 링크) 동기화
    private void syncCodes(com.teamalgo.algo.domain.record.Record record, List<RecordCodeDTO> dtos) {
        Map<Long, RecordCode> current = record.getCodes().stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(RecordCode::getId, Function.identity()));

        List<RecordCode> next = new ArrayList<>();
        for (RecordCodeDTO dto : dtos) {
            if (dto.getId() != null && current.containsKey(dto.getId())) {
                RecordCode entity = current.get(dto.getId());
                entity.update(dto.getLanguage(), dto.getCode(), dto.getVerdict(), dto.getSnippetOrder());
                next.add(entity);
            } else {
                next.add(dto.toEntity(record));
            }
        }
        record.getCodes().clear();
        record.getCodes().addAll(next);
    }


    private void syncSteps(com.teamalgo.algo.domain.record.Record record, List<RecordStepDTO> dtos) {
        Map<Long, RecordStep> current = record.getSteps().stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toMap(RecordStep::getId, Function.identity()));

        List<RecordStep> next = new ArrayList<>();
        for (RecordStepDTO dto : dtos) {
            if (dto.getId() != null && current.containsKey(dto.getId())) {
                RecordStep entity = current.get(dto.getId());
                entity.update(dto.getStepOrder(), dto.getText());
                next.add(entity);
            } else {
                next.add(dto.toEntity(record));
            }
        }
        record.getSteps().clear();
        record.getSteps().addAll(next);
    }


    private void syncIdeas(com.teamalgo.algo.domain.record.Record record, List<RecordCoreIdeaDTO> dtos) {
        Map<Long, RecordCoreIdea> current = record.getIdeas().stream()
                .filter(i -> i.getId() != null)
                .collect(Collectors.toMap(RecordCoreIdea::getId, Function.identity()));

        List<RecordCoreIdea> next = new ArrayList<>();
        for (RecordCoreIdeaDTO dto : dtos) {
            if (dto.getId() != null && current.containsKey(dto.getId())) {
                RecordCoreIdea entity = current.get(dto.getId());
                entity.update(dto.getContent());
                next.add(entity);
            } else {
                next.add(dto.toEntity(record));
            }
        }
        record.getIdeas().clear();
        record.getIdeas().addAll(next);
    }


    private void syncLinks(com.teamalgo.algo.domain.record.Record record, List<RecordLinkDTO> dtos) {
        Map<Long, RecordLink> current = record.getLinks().stream()
                .filter(l -> l.getId() != null)
                .collect(Collectors.toMap(RecordLink::getId, Function.identity()));

        List<RecordLink> next = new ArrayList<>();
        for (RecordLinkDTO dto : dtos) {
            if (dto.getId() != null && current.containsKey(dto.getId())) {
                RecordLink entity = current.get(dto.getId());
                entity.update(dto.getUrl());
                next.add(entity);
            } else {
                next.add(dto.toEntity(record));
            }
        }
        record.getLinks().clear();
        record.getLinks().addAll(next);
    }


    // 매핑 헬퍼
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
