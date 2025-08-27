package com.teamalgo.algo.service.stats;

import com.teamalgo.algo.domain.stats.StatsDaily;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.StreakRecordDTO;
import com.teamalgo.algo.dto.response.StreakCalendarResponse;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.repository.StatsDailyRepository;
import com.teamalgo.algo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Transactional
    public StreakCalendarResponse getYearlyStreak(Long userId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusYears(1).plusDays(1);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 1년치 기록 가져오기
        List<StatsDaily> records = statsDailyRepository.findByUserAndStatDateBetween(user, start, end);

        // 날짜별 count 매핑
        Map<LocalDate, Integer> countMap = records.stream()
                .collect(Collectors.toMap(
                        StatsDaily::getStatDate,
                        StatsDaily::getRecordedCnt
                ));

        // start ~ end 까지 빈 날짜는 0으로 채우기
        List<StreakRecordDTO> streaks = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            streaks.add(new StreakRecordDTO(date, countMap.getOrDefault(date, 0)));
        }

        int totalCount = streaks.stream().mapToInt(StreakRecordDTO::getCount).sum();

        return new StreakCalendarResponse(userId, start, end, streaks, totalCount);
    }

}
