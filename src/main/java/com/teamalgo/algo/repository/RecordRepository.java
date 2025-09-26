package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.record.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {

    Optional<Record> findById(Long id);
    Optional<Record> findByIdAndIsDraftFalse(Long id);
    List<Record> findByUserId(Long userId);
    Page<Record> findByUserIdAndIsDraftFalse(Long userId, Pageable pageable);
    Page<Record> findByUserIdAndIsDraftTrue(Long userId, Pageable pageable);
    Optional<Record> findByIdAndUserId(Long recordId, Long userId);

    @Query("""
    SELECT r
    FROM Record r
    LEFT JOIN r.bookmarks b
    LEFT JOIN r.problem p
    LEFT JOIN r.recordCategories rc
    LEFT JOIN rc.category c
    LEFT JOIN r.user u
    WHERE r.isDraft = false AND r.isPublished = true
      AND (:search IS NULL OR
           CASE
               WHEN r.customTitle IS NOT NULL AND r.customTitle <> ''
               THEN r.customTitle
               ELSE p.title
             END LIKE :search)
      AND (:url IS NULL OR p.url = :url)
      AND (:author IS NULL OR u.username = :author)
      AND (:category IS NULL OR c.name = :category)
      AND (:startDate IS NULL OR :endDate IS NULL OR r.createdAt BETWEEN :startDate AND :endDate)
    GROUP BY r
    ORDER BY COUNT(b) DESC, LENGTH(r.detail) DESC
    """)
    Page<Record> findPopularWithFilters(
            @Param("search") String search,
            @Param("url") String url,
            @Param("author") String author,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("""
        SELECT r FROM Record r
        JOIN r.recordCategories rc
        JOIN rc.category c
        WHERE r.user.id = :userId
        AND r.isDraft = false
        AND c.id = :categoryId
    """)
    Page<Record> findByUserIdAndIsDraftFalseAndCategoryId(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    @Query("""
        SELECT r
        FROM Record r
        WHERE r.user.id = :userId
          AND r.isDraft = false
          AND NOT EXISTS (
              SELECT 1 FROM ReviewLog rl
              WHERE rl.user.id = :userId
                AND rl.record.id = r.id
          )
    """)
    List<Record> findUnreviewedRecords(@Param("userId") Long userId);

}
