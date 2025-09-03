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
    private String source;   // 백준, 프로그래머스 등

    private Long numericId;  // BOJ, Programmers, CodeUp, Codeforces

    private String slugId;   // LeetCode 등 slug 기반

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String title;    // 공식/공용 제목

    // 나중에 문제 번호 보여줄 때 사용
    public String getDisplayId() {
        return numericId != null ? String.valueOf(numericId) : null;
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }
}



