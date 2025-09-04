package com.teamalgo.algo.domain.record;

import com.teamalgo.algo.domain.bookmark.Bookmark;
import com.teamalgo.algo.domain.category.RecordCategory;
import com.teamalgo.algo.domain.problem.Problem;
import com.teamalgo.algo.domain.review.ReviewLog;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    // 사용자별 개별 커스텀 제목
    private String customTitle;

    // Steps
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<RecordStep> steps = new ArrayList<>();

    // Codes
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("snippetOrder ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<RecordCode> codes = new ArrayList<>();

    // Links
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<RecordLink> links = new ArrayList<>();

    // Ideas
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<RecordCoreIdea> ideas = new ArrayList<>();

    // Categories
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Builder.Default
    private List<RecordCategory> recordCategories = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLog> reviewLogs = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    // --- 도메인 메서드 ---
    public void updateDetail(String detail) {
        this.detail = detail;
    }

    public void updateDraft(boolean isDraft) {
        this.isDraft = isDraft;
    }

    public void updatePublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    public void updateCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }
}
