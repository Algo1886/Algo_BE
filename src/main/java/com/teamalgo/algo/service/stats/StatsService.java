package com.teamalgo.algo.service.stats;

import com.teamalgo.algo.domain.stats.StatsDaily;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.repository.StatsDailyRepository;
import com.teamalgo.algo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final StatsDailyRepository statsDailyRepository;

    @Transactional
    public void updateStats(User user, boolean isSuccess) {
        LocalDate today = LocalDate.now();

        // Daily Stats 업데이트
        StatsDaily stats = statsDailyRepository.findByUserAndStatDate(user, today)
                .orElseGet(() -> statsDailyRepository.save(
                        StatsDaily.builder()
                                .user(user)
                                .statDate(today)
                                .recordedCnt(0)
                                .successCnt(0)
                                .failCnt(0)
                                .build()
                ));

        stats.setRecordedCnt(stats.getRecordedCnt() + 1);
        if (isSuccess) {
            stats.setSuccessCnt(stats.getSuccessCnt() + 1);
        } else {
            stats.setFailCnt(stats.getFailCnt() + 1);
        }

        // 스트릭 업데이트
        if (!today.equals(user.getLastRecordedDate())) {
            if (today.minusDays(1).equals(user.getLastRecordedDate())) {
                user.setCurrentStreak(user.getCurrentStreak() + 1);
            } else {
                user.setCurrentStreak(1);
            }
            user.setMaxStreak(Math.max(user.getMaxStreak(), user.getCurrentStreak()));
            user.setLastRecordedDate(today);
        }

        userRepository.save(user);
    }
}
