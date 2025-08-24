package com.teamalgo.algo.domain.record;

import com.teamalgo.algo.domain.problem.Problem;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.request.RecordUpdateRequest;
import com.teamalgo.algo.global.entity.BaseEntity;
import com.teamalgo.algo.domain.category.RecordCategory;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "record")
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(nullable = false)
    private String status; // success | fail

    private Integer difficulty;

    @Lob
    private String detail;

    @Column(name = "is_draft", nullable = false)
    private boolean isDraft;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordStep> steps = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordCode> codes = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordLink> links = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordCoreIdea> ideas = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordCategory> recordCategories = new ArrayList<>();

    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    public void applyPatch(RecordUpdateRequest request) {
        if (request.getDetail() != null) {
            this.detail = request.getDetail();
        }
        if (request.getIsDraft() != null) {
            this.isDraft = request.getIsDraft();
        }
        if (request.getIsPublished() != null) {
            this.isPublished = request.getIsPublished();
        }
        // Codes, Steps, Ideas, Links는 서비스 계층에서 sync 메서드로 처리
    }
}
