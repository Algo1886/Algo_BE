package com.teamalgo.algo.service.record;

import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.review.ReviewLog;
import com.teamalgo.algo.dto.RecordDTO;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.repository.RecordRepository;
import com.teamalgo.algo.repository.ReviewLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final RecordRepository recordRepository;
    private final ReviewLogRepository reviewLogRepository;

    // 경과일수 점수 계산
    private int calculateElapsedScore(LocalDateTime createdAt) {
        long days = ChronoUnit.DAYS.between(createdAt.toLocalDate(), LocalDate.now());
        if (days >= 30) return 5;
        if (days >= 14) return 4;
        if (days >= 7) return 3;
        if (days >= 3) return 2;
        return 1;
    }

    // 풀이 복잡도 점수 계산
    private int calculateComplexityScore(Record record) {
        int score = 1;
        if (record.getCodes() != null && record.getCodes().size() > 2) score++;
        if (record.getDetail() != null && record.getDetail().length() > 200) score++;
        if (record.getLinks() != null && !record.getLinks().isEmpty()) score += 2;
        return Math.min(score, 5);
    }

    // 추천 복습 문제 조회
    public List<RecordDTO> getRecommendedReviews(Long userId) {
        List<Record> records = recordRepository.findByUserId(userId);

        return records.stream()
                // 이미 복습한 건 제외
                .filter(record -> !reviewLogRepository.existsByUserIdAndRecordId(userId, record.getId()))
                .map(record -> {
                    int difficulty = record.getDifficulty() != null ? record.getDifficulty() : 1;
                    int complexity = calculateComplexityScore(record);
                    int elapsed = calculateElapsedScore(record.getCreatedAt());

                    double score = (difficulty * 0.5) + (complexity * 0.3) + (elapsed * 0.2);
                    return Map.entry(score, record);
                })
                .sorted((a, b) -> Double.compare(b.getKey(), a.getKey())) // 가중치 점수순 정렬
                .limit(12)
                .map(entry -> RecordDTO.from(entry.getValue()))
                .toList();
    }

    // 복습 완료
    @Transactional
    public void completeReview(Long recordId, Long userId) {
        Record record = recordRepository.findByIdAndUserId(recordId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        // 복습 기록이 이미 있으면 에러
        if (reviewLogRepository.existsByUserIdAndRecordIdAndReviewedAt(userId, recordId, LocalDate.now())) {
            throw new CustomException(ErrorCode.ALREADY_REVIEWED);
        }

        ReviewLog reviewLog = ReviewLog.builder()
                .user(record.getUser())
                .record(record)
                .reviewedAt(LocalDate.now())
                .build();

        reviewLogRepository.save(reviewLog);
    }
}