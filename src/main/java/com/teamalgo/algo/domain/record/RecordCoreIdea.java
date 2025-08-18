package com.teamalgo.algo.domain.record;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "record_core_idea")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecordCoreIdea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id")
    private Record record;

    @Lob
    @Column(nullable = false)
    private String content;
}
