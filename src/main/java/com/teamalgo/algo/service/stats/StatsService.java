package com.teamalgo.algo.service.stats;

import com.teamalgo.algo.domain.stats.StatsDaily;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.StreakRecordDTO;
import com.teamalgo.algo.dto.response.StreakCalendarResponse;
import com.teamalgo.algo.dto.response.UserStatsResponse;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final StatsDailyRepository statsDailyRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RecordCoreIdeaRepository recordCoreIdeaRepository;
    private final RecordCategoryRepository recordCategoryRepository;

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

    public UserStatsResponse getUserStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(6);

        // records 통계
        Long totalRecords = Optional.ofNullable(statsDailyRepository.getTotalRecords(user)).orElse(0L);
        Long thisWeekRecords = Optional.ofNullable(statsDailyRepository.getThisWeekRecords(user, startOfWeek)).orElse(0L);

        Object result = statsDailyRepository.getSuccessAndFail(user);
        Object[] row = (Object[]) result;

        Long successCount = row[0] != null ? ((Number) row[0]).longValue() : 0L;
        Long failCount   = row[1] != null ? ((Number) row[1]).longValue() : 0L;

        Double successRate = (successCount + failCount) > 0
                ? (successCount * 100.0 / (successCount + failCount))
                : 0.0;

        // bookmarks
        Long totalBookmarks = bookmarkRepository.countByUser(user);
        Long thisWeekBookmarks = bookmarkRepository.countThisWeekByUser(user, startOfWeek.atStartOfDay());

        // ideas
        Long totalIdeas = recordCoreIdeaRepository.countByUser(user);
        List<Object[]> topIdeaCategoryResult = recordCoreIdeaRepository.findTopCategoryByUser(user, PageRequest.of(0, 1));

        UserStatsResponse.Ideas.TopCategory topCategoryDTO = null;
        if (!topIdeaCategoryResult.isEmpty()) {
            Object[] ideaRow = topIdeaCategoryResult.get(0);
            String name = (String) ideaRow[0];
            Long count = ((Number) ideaRow[1]).longValue();
            Double ratio = totalIdeas > 0 ? (count * 100.0 / totalIdeas) : 0.0;

            topCategoryDTO = UserStatsResponse.Ideas.TopCategory.builder()
                    .name(name)
                    .ratio(ratio)
                    .build();
        }

        // categories
        List<Object[]> mostSolvedCategoryResult = recordCategoryRepository.findMostSolvedByUser(user, PageRequest.of(0, 1));
        UserStatsResponse.Categories.MostSolvedCategory mostSolvedCategoryDTO = null;
        if (!mostSolvedCategoryResult.isEmpty()) {
            Object[] categoryRow = mostSolvedCategoryResult.get(0);
            String name = (String) categoryRow[0];
            Long count = ((Number) categoryRow[1]).longValue();
            Double ratio = totalRecords > 0 ? (count * 100.0 / totalRecords) : 0.0;

            mostSolvedCategoryDTO = UserStatsResponse.Categories.MostSolvedCategory.builder()
                    .name(name)
                    .count(count)
                    .ratio(ratio)
                    .build();
        }

        return UserStatsResponse.of(
                user,
                user.getCurrentStreak(),
                user.getMaxStreak(),
                totalRecords,
                thisWeekRecords,
                successCount,
                successRate,
                totalBookmarks,
                thisWeekBookmarks,
                totalIdeas,
                topCategoryDTO,
                mostSolvedCategoryDTO
        );
    }

}
