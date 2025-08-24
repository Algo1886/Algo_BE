package com.teamalgo.algo.service.record;

import com.teamalgo.algo.dto.CoreIdeaDTO;
import com.teamalgo.algo.repository.RecordCoreIdeaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoreIdeaService {

    private final RecordCoreIdeaRepository recordCoreIdeaRepository;

    public List<CoreIdeaDTO> getUserIdeas(Long userId) {
        return recordCoreIdeaRepository.findByRecordUserId(userId)
                .stream()
                .map(CoreIdeaDTO::fromEntity)
                .toList();
    }
}

