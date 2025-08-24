package com.teamalgo.algo.domain.category;

import com.teamalgo.algo.domain.record.Record;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "record_category",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"record_id", "category_id"})
        }
)
public class RecordCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id")
    private Record record;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;
}
