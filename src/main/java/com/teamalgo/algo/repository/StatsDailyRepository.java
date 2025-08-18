package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.stats.StatsDaily;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsDailyRepository extends JpaRepository<StatsDaily, Long> {
}
