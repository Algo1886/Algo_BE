package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.record.RecordCoreIdea;
import com.teamalgo.algo.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordCoreIdeaRepository extends JpaRepository<RecordCoreIdea, Long> {

    // 특정 유저의 레코드에서 나온 아이디어만 조회
    List<RecordCoreIdea> findByRecordUserId(Long userId);

    @Query("""
        SELECT c.name, COUNT(idea.id) as ideaCount
        FROM RecordCoreIdea idea
        JOIN idea.record r
        JOIN RecordCategory rc ON rc.record = r
        JOIN rc.category c
        WHERE r.user = :user
        GROUP BY c.name
        ORDER BY ideaCount DESC
    """)
    List<Object[]> findTopCategoryByUser(@Param("user") User user, Pageable pageable);

    // 사용자 별 작성한 핵심 아이디어 개수
    @Query("SELECT COUNT(idea) FROM RecordCoreIdea idea WHERE idea.record.user = :user")
    Long countByUser(@Param("user") User user);

}
