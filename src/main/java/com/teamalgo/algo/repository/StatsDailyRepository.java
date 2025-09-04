package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.stats.StatsDaily;
import com.teamalgo.algo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StatsDailyRepository extends JpaRepository<StatsDaily, Long> {
    Optional<StatsDaily> findByUserAndStatDate(User user, LocalDate statDate);

    List<StatsDaily> findByUserAndStatDateBetween(User user, LocalDate start, LocalDate end);

    // 전체 기록 수
    @Query("SELECT SUM(s.recordedCnt) FROM StatsDaily s WHERE s.user = :user")
    Long getTotalRecords(@Param("user") User user);

    // 최근 1주일 기록 수
    @Query("SELECT SUM(s.recordedCnt) FROM StatsDaily s WHERE s.user = :user AND s.statDate >= :startDate")
    Long getThisWeekRecords(@Param("user") User user, @Param("startDate") LocalDate startDate);

    // 성공/실패 카운트
    @Query("SELECT SUM(s.successCnt), SUM(s.failCnt) FROM StatsDaily s WHERE s.user = :user")
    Object getSuccessAndFail(@Param("user") User user);

    List<StatsDaily> findByUserOrderByStatDateAsc(User user);

}
