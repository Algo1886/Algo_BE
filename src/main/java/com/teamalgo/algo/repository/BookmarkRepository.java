package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.bookmark.Bookmark;
import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 단일 조회에서 북마크 여부 확인할 때
    boolean existsByUserAndRecord(User user, Record record);
    // 북마크 엔터티 가져와서 삭제할 때
    Optional<Bookmark> findByUserAndRecord(User user, Record record);
    // RecordDTO 형태로 북마크 목록 반환
    List<Bookmark> findByUser(User user);
}
