package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.problem.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Optional<Problem> findByUrl(String url);
}
