package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.record.RecordCoreIdea;
import com.teamalgo.algo.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RecordCoreIdeaRepository extends JpaRepository<RecordCoreIdea, Long> {

    // 특정 유저의 레코드에서 나온 아이디어 (draft 제외)
    Page<RecordCoreIdea> findByRecordUserIdAndRecordIsDraftFalse(Long userId, Pageable pageable);

    // 특정 카테고리 + draft 제외
    Page<RecordCoreIdea> findByRecordUserIdAndRecordIsDraftFalseAndRecord_RecordCategories_Category_Name(
            Long userId, String category, Pageable pageable);

    // 최신순 + draft 제외
    Page<RecordCoreIdea> findByRecordUserIdAndRecordIsDraftFalseOrderByRecordCreatedAtDesc(
            Long userId, Pageable pageable);

    @Query("""
        SELECT c.name, COUNT(idea.id) as ideaCount
        FROM RecordCoreIdea idea
        JOIN idea.record r
        JOIN RecordCategory rc ON rc.record = r
        JOIN rc.category c
        WHERE r.user = :user
        AND r.isDraft = false
        GROUP BY c.name
        ORDER BY ideaCount DESC
    """)
    List<Object[]> findTopCategoryByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT COUNT(idea) FROM RecordCoreIdea idea WHERE idea.record.user = :user AND idea.record.isDraft = false")
    Long countByUser(@Param("user") User user);
}
