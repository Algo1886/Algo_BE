package com.teamalgo.algo.domain.record;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "record_link")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecordLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id")
    private Record record;

    @Column(nullable = false)
    private String url;
}
