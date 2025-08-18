package com.teamalgo.algo.domain.review;

import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "review_log",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "record_id", "reviewedAt"})
)
public class ReviewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id")
    private Record record;

    @Column(nullable = false)
    private LocalDate reviewedAt;
}
