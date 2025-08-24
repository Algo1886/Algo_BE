package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.category.RecordCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordCategoryRepository extends JpaRepository<RecordCategory, Long> {
}
