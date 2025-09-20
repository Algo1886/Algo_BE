package com.teamalgo.algo.service.record;

import com.teamalgo.algo.dto.CoreIdeaDTO;
import com.teamalgo.algo.repository.RecordCoreIdeaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
        return recordCoreIdeaRepository
                .findByRecordUserIdAndRecordIsDraftFalseAndContentIsNotNullAndContentNot(userId, "", pageable)
                .map(CoreIdeaDTO::fromEntity);
    }

    // 핵심 아이디어 목록 조회 (카테고리 선택 O)
    public Page<CoreIdeaDTO> getUserIdeas(Long userId, Pageable pageable, String category) {
        if (category == null || category.isBlank()) {
            return getUserIdeas(userId, pageable);
        }

        return recordCoreIdeaRepository
                .findByRecordUserIdAndRecordIsDraftFalseAndRecord_RecordCategories_Category_NameAndContentIsNotNullAndContentNot(
                        userId, category, "", pageable
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


