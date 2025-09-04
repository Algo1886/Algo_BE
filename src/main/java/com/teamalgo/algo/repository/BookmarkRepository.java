package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.bookmark.Bookmark;
import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 단일 조회에서 북마크 여부 확인할 때
    boolean existsByUserAndRecordAndRecordIsDraftFalse(User user, Record record);

    // 북마크 엔터티 가져와서 삭제할 때
    Optional<Bookmark> findByUserAndRecordAndRecordIsDraftFalse(User user, Record record);

    // 카테고리별 북마크 목록
    Page<Bookmark> findByUserAndRecordIsDraftFalseAndRecord_RecordCategories_Category_Name(
            User user, String category, Pageable pageable);

    // 전체 북마크 목록
    Page<Bookmark> findByUserAndRecordIsDraftFalse(User user, Pageable pageable);


    // 사용자 별 전체 북마크 수
    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.user = :user AND b.record.isDraft = false")
    Long countByUser(@Param("user") User user);

    // 최근 1주일 북마크 수
    @Query("""
        SELECT COUNT(b) FROM Bookmark b 
        WHERE b.user = :user 
          AND b.record.isDraft = false 
          AND b.createdAt >= :startDate
    """)
    Long countThisWeekByUser(@Param("user") User user, @Param("startDate") LocalDateTime startDate);
}

