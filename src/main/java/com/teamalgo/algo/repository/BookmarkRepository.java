package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.bookmark.Bookmark;
import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 단일 조회에서 북마크 여부 확인할 때
    boolean existsByUserAndRecord(User user, Record record);
    // 북마크 엔터티 가져와서 삭제할 때
    Optional<Bookmark> findByUserAndRecord(User user, Record record);
    // RecordDTO 형태로 북마크 목록 반환
    List<Bookmark> findByUser(User user);
    // 사용자 별 전체 북마크 수
    Long countByUser(User user);
    // 사용자 별 최근 1주일 북마크 수
    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.user = :user AND b.createdAt >= :startDate")
    Long countThisWeekByUser(@Param("user") User user, @Param("startDate") LocalDateTime startDate);

}
