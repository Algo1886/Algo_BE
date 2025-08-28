package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.review.ReviewLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ReviewLogRepository extends JpaRepository<ReviewLog, Long> {

    boolean existsByUserIdAndRecordIdAndReviewedAt(Long userId, Long recordId, LocalDate reviewedAt);

    boolean existsByUserIdAndRecordId(Long userId, Long recordId);

    Optional<ReviewLog> findByUserIdAndRecordIdAndReviewedAt(Long userId, Long recordId, LocalDate reviewedAt);
}
