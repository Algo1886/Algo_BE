package com.teamalgo.algo.dto.response;

import com.teamalgo.algo.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStatsResponse {

    private Profile profile;
    private Records records;
    private Bookmarks bookmarks;
    private Ideas ideas;
    private Categories categories;

    @Getter
    @Builder
    public static class Profile {
        private Long id;
        private String username;
        private String avatarUrl;
        private int streakDays;
        private int maxStreakDays;
    }

    @Getter
    @Builder
    public static class Records {
        private long totalCount;
        private long thisWeekCount;
        private long successCount;
        private double successRate;
    }

    @Getter
    @Builder
    public static class Bookmarks {
        private long totalCount;
        private long thisWeekCount;
    }

    @Getter
    @Builder
    public static class Ideas {
        private long totalCount;
        private TopCategory topCategory;

        @Getter
        @Builder
        public static class TopCategory {
            private String name;
            private double ratio;
        }
    }

    @Getter
    @Builder
    public static class Categories {
        private MostSolvedCategory mostSolvedCategory;

        @Getter
        @Builder
        public static class MostSolvedCategory {
            private String name;
            private long count;
            private double ratio;
        }
    }

    public static UserStatsResponse of(User user,
                                       int streakDays, int maxStreakDays,
                                       long totalRecords, long thisWeekRecords, long successCount, double successRate,
                                       long totalBookmarks, long thisWeekBookmarks,
                                       long totalIdeas, Ideas.TopCategory topCategory,
                                       Categories.MostSolvedCategory mostSolvedCategory) {
        return UserStatsResponse.builder()
                .profile(Profile.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .avatarUrl(user.getAvatarUrl())
                        .streakDays(streakDays)
                        .maxStreakDays(maxStreakDays)
                        .build())
                .records(Records.builder()
                        .totalCount(totalRecords)
                        .thisWeekCount(thisWeekRecords)
                        .successCount(successCount)
                        .successRate(successRate)
                        .build())
                .bookmarks(Bookmarks.builder()
                        .totalCount(totalBookmarks)
                        .thisWeekCount(thisWeekBookmarks)
                        .build())
                .ideas(Ideas.builder()
                        .totalCount(totalIdeas)
                        .topCategory(topCategory)
                        .build())
                .categories(Categories.builder()
                        .mostSolvedCategory(mostSolvedCategory)
                        .build())
                .build();
    }
}
