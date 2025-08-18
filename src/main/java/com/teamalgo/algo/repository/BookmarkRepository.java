package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.bookmark.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
