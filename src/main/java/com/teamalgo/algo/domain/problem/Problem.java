package com.teamalgo.algo.domain.problem;

import com.teamalgo.algo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "problem",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"source", "numericId", "slugId"})
        }
)
public class Problem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;

    private Long numericId;   // BOJ, Programmers, CodeUp, Codeforces

    private String slugId;    // LeetCode 등 slug 기반

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String title;

    public String getDisplayId() {
        return numericId != null ? String.valueOf(numericId) : null;
    }
}
