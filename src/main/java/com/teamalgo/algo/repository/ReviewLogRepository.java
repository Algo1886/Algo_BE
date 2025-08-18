package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.review.ReviewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLogRepository extends JpaRepository<ReviewLog, Long> {
}
