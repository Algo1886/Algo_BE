package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.record.Record;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {

    @EntityGraph(attributePaths = {"codes", "steps", "ideas", "links"})
    Optional<Record> findById(Long id);

}
