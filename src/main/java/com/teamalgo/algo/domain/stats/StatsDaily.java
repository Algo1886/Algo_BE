package com.teamalgo.algo.domain.stats;

import com.teamalgo.algo.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "stats_daily",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "statDate"})
)
public class StatsDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate statDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int recordedCnt;

    @Column(nullable = false)
    private int successCnt;

    @Column(nullable = false)
    private int failCnt;
}
