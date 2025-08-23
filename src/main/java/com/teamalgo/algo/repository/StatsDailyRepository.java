package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.stats.StatsDaily;
import com.teamalgo.algo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface StatsDailyRepository extends JpaRepository<StatsDaily, Long> {
    Optional<StatsDaily> findByUserAndStatDate(User user, LocalDate statDate);
}
