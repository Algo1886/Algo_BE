package com.teamalgo.algo.domain.user;

import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.bookmark.Bookmark;
import com.teamalgo.algo.domain.review.ReviewLog;
import com.teamalgo.algo.domain.stats.StatsDaily;
import com.teamalgo.algo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = {"provider", "providerId"})
        }
)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String username;

    private String avatarUrl;

    @Column(nullable = false, length = 20)
    private String provider;  // google, kakao, github

    @Column(nullable = false, unique = true, length = 100)
    private String providerId;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int currentStreak;   // 현재 스트릭 (연속 일수)

    @Column(nullable = false)
    @ColumnDefault("0")
    private int maxStreak;       // 최장 스트릭

    @Column
    private LocalDate lastRecordedDate; // 마지막 기록한 날짜

    // 최장 스트릭 기간
    @Column
    private LocalDate maxStreakStartDate;
    @Column
    private LocalDate maxStreakEndDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Record> records = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLog> reviewLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatsDaily> stats = new ArrayList<>();

    public void update(String username, String avatarUrl) {
        if (username != null) this.username = username;
        if (avatarUrl != null) this.avatarUrl = avatarUrl;
    }
}