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
        return getUserIdeas(userId, pageable, null);
    }
    // 핵심 아이디어 목록 조회 (카테고리 선택 O)
    public Page<CoreIdeaDTO> getUserIdeas(Long userId, Pageable pageable, String category) {
        if (category == null || category.isBlank()) {
            return recordCoreIdeaRepository.findByRecordUserId(userId, pageable)
                    .map(CoreIdeaDTO::fromEntity);
        }

        return recordCoreIdeaRepository.findByRecordUserIdAndRecord_RecordCategories_Category_Name(
                        userId, category, pageable)
                .map(CoreIdeaDTO::fromEntity);
    }
    public List<CoreIdeaDTO> getRecentIdeas(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return recordCoreIdeaRepository.findByRecordUserIdOrderByRecordCreatedAtDesc(userId, pageable)
                .stream()
                .map(CoreIdeaDTO::fromEntity)
                .toList();
    }

}

