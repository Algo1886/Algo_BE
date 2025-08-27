package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.category.RecordCategory;
import com.teamalgo.algo.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordCategoryRepository extends JpaRepository<RecordCategory, Long> {

    @Query("""
        SELECT c.name, COUNT(r.id) as solvedCount
        FROM RecordCategory rc
        JOIN rc.record r
        JOIN rc.category c
        WHERE r.user = :user
        GROUP BY c.name
        ORDER BY solvedCount DESC
    """)
    List<Object[]> findMostSolvedByUser(@Param("user") User user, Pageable pageable);

}
