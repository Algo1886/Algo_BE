package com.teamalgo.algo.repository;

import com.teamalgo.algo.domain.record.RecordCoreIdea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordCoreIdeaRepository extends JpaRepository<RecordCoreIdea, Long> {

    // 특정 유저의 레코드에서 나온 아이디어만 조회
    List<RecordCoreIdea> findByRecordUserId(Long userId);
}
