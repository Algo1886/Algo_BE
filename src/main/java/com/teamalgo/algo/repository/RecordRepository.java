package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.record.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {

    Optional<Record> findById(Long id);

    List<Record> findByUserId(Long userId);

    Optional<Record> findByIdAndUserId(Long recordId, Long userID);

    Page<Record> findByUserId(Long userId, Pageable pageable);

    Page<com.teamalgo.algo.domain.record.Record> findByUserIdAndIsDraftTrue(Long userId, Pageable pageable);


}
