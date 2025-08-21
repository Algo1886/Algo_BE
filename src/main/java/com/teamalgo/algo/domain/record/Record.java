package com.teamalgo.algo.domain.record;

import com.teamalgo.algo.domain.problem.Problem;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.RecordCodeDTO;
import com.teamalgo.algo.dto.RecordStepDTO;
import com.teamalgo.algo.dto.RecordCoreIdeaDTO;
import com.teamalgo.algo.dto.RecordLinkDTO;
import com.teamalgo.algo.dto.request.RecordUpdateRequest;
import com.teamalgo.algo.global.entity.BaseEntity;
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

    @Column(nullable = false)
    private boolean isDraft;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordStep> steps = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordCode> codes = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordLink> links = new ArrayList<>();

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordCoreIdea> ideas = new ArrayList<>();

    @Column(nullable = false)
    private boolean isPublished;

    public void applyPatch(RecordUpdateRequest request) {
        if (request.getDetail() != null) {
            this.detail = request.getDetail();  // detail 수정
        }

        if (request.getCodes() != null) {
            updateCodes(request.getCodes());  // codes 수정
        }

        if (request.getSteps() != null) {
            updateSteps(request.getSteps());  // steps 수정
        }

        if (request.getIdeas() != null) {
            updateIdeas(request.getIdeas());  // ideas 수정
        }

        if (request.getLinks() != null) {
            updateLinks(request.getLinks());  // links 수정
        }

        if (request.getIsDraft() != null) {
            this.isDraft = request.getIsDraft();  // isDraft 수정
        }

        if (request.getIsPublished() != null) {
            this.isPublished = request.getIsPublished();  // isPublished 수정
        }
    }

    // Codes, Steps, Ideas, Links 업데이트 로직
    private void updateCodes(List<RecordCodeDTO> newCodes) {
        // 새로운 코드들을 엔티티에 반영 (추가, 수정, 삭제)
    }

    private void updateSteps(List<RecordStepDTO> newSteps) {
        // 새로운 스텝들을 엔티티에 반영 (추가, 수정, 삭제)
    }

    private void updateIdeas(List<RecordCoreIdeaDTO> newIdeas) {
        // 새로운 아이디어들을 엔티티에 반영 (추가, 수정, 삭제)
    }

    private void updateLinks(List<RecordLinkDTO> newLinks) {
        // 새로운 링크들을 엔티티에 반영 (추가, 수정, 삭제)
    }
}
