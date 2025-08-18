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
                @UniqueConstraint(columnNames = {"source", "externalId"})
        }
)
public class Problem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String externalId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String title;
}
