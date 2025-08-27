package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.record.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {

    // Hibernate가 @Fetch(FetchMode.SUBSELECT)로 컬렉션을 한 번에 모아 가져옴
    Optional<Record> findById(Long id);

}
