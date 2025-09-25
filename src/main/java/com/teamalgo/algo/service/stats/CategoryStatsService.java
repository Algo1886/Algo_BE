package com.teamalgo.algo.service.stats;

import com.teamalgo.algo.dto.response.CategoryStatsResponse;
import com.teamalgo.algo.repository.RecordCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryStatsService {

    private final RecordCategoryRepository recordCategoryRepository;

    public List<CategoryStatsResponse> getCategoryStats(Long userId) {
        List<CategoryStatsResponse> results = recordCategoryRepository.findCategoryCountsByUserId(userId);

        Long total = results.stream()
                        .mapToLong(CategoryStatsResponse::getCount)
                        .sum();

        return results.stream()
                .map(r -> new CategoryStatsResponse(
                        r.getName(),
                        r.getCount(),
                        Math.round(((double) r.getCount() / total) * 1000) / 10.0
                ))
                .toList();
    }

}
