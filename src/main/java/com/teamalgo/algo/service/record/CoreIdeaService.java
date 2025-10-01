package com.teamalgo.algo.service.record;

import com.teamalgo.algo.dto.CoreIdeaDTO;
import com.teamalgo.algo.repository.RecordCoreIdeaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoreIdeaService {

    private final RecordCoreIdeaRepository recordCoreIdeaRepository;

    // 핵심 아이디어 목록 조회 (카테고리 선택 X)
    public Page<CoreIdeaDTO> getUserIdeas(Long userId, Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "record.createdAt") // 최신순
        );
        return recordCoreIdeaRepository
                .findByRecordUserIdAndRecordIsDraftFalseAndContentIsNotNullAndContentNot(userId, "", sorted)
                .map(CoreIdeaDTO::fromEntity);
    }

    // 핵심 아이디어 목록 조회 (카테고리 선택 O)
    public Page<CoreIdeaDTO> getUserIdeas(Long userId, Pageable pageable, Long categoryId) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "record.createdAt") // 최신순
        );

        if (categoryId == null) {
            return getUserIdeas(userId, sorted);
        }

        return recordCoreIdeaRepository
                .findByRecordUserIdAndRecordIsDraftFalseAndRecord_RecordCategories_Category_IdAndContentIsNotNullAndContentNot(
                        userId, categoryId, "",sorted
                )
                .map(CoreIdeaDTO::fromEntity);
    }

    // 최신 아이디어 조회 (draft 제외)
    public List<CoreIdeaDTO> getRecentIdeas(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return recordCoreIdeaRepository
                .findByRecordUserIdAndRecordIsDraftFalseAndContentIsNotNullAndContentNotOrderByRecordCreatedAtDesc(
                        userId, "", pageable
                )
                .stream()
                .map(CoreIdeaDTO::fromEntity)
                .toList();
    }
}


