package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.category.ProblemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemCategoryRepository extends JpaRepository<ProblemCategory, Long> {
}
