package com.teamalgo.algo.domain.record;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "record_code",
        uniqueConstraints = @UniqueConstraint(columnNames = {"record_id", "snippetOrder"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecordCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id")
    private Record record;

    @Column(nullable = false)
    private String language;

    @Lob
    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String verdict; // pass | fail

    @Column(nullable = false)
    private int snippetOrder;
}
