package com.teamalgo.algo.service.problem;

import com.teamalgo.algo.dto.response.ProblemPreviewResponse;
import com.teamalgo.algo.global.common.util.ProblemFetcher;
import com.teamalgo.algo.global.common.util.ProblemSourceDetector;
import com.teamalgo.algo.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemFetcher problemFetcher;

    // 문제 URL을 기반으로 문제 정보를 조회 (DB 저장은 X)

    public ProblemPreviewResponse fetchProblemInfo(String url) {
        String source = ProblemSourceDetector.detectSource(url);

        String title = null;
        if ("백준".equals(source)) {
            Long numericId = ProblemSourceDetector.extractNumericId(url, source);
            if (numericId != null) {
                title = problemFetcher.fetchBaekjoonTitle(numericId);
            }
        } else if ("프로그래머스".equals(source)) {
            title = problemFetcher.fetchProgrammersTitle(url);
        }

        return ProblemPreviewResponse.builder()
                .title(title)
                .source(source)
                .url(url)
                .build();
    }
}
