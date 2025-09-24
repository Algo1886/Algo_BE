package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.category.RecordCategory;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.response.CategoryStatsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordCategoryRepository extends JpaRepository<RecordCategory, Long> {

    @Query("""
    SELECT c.name, COUNT(r.id) as solvedCount, MAX(r.createdAt) as lastSolvedDate
    FROM RecordCategory rc
    JOIN rc.record r
    JOIN rc.category c
    WHERE r.user = :user
        AND r.isDraft = false
    GROUP BY c.name
    ORDER BY solvedCount DESC, lastSolvedDate DESC
""")
    List<Object[]> findMostSolvedByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT new com.teamalgo.algo.dto.response.CategoryStatsResponse(c.slug, c.name, COUNT(rc)) " +
            "FROM RecordCategory rc " +
            "JOIN rc.category c " +
            "WHERE rc.record.user.id = :userId " +
            "AND rc.record.isDraft = false " +
            "GROUP BY c.slug, c.name")
    List<CategoryStatsResponse> findCategoryCountsByUserId(@Param("userId") Long userId);

}
