package com.teamalgo.algo.controller;

import com.teamalgo.algo.dto.response.ProblemPreviewResponse;
import com.teamalgo.algo.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    // 문제 URL로 문제 정보 미리보기
    @GetMapping("/fetch")
    public ProblemPreviewResponse fetchProblem(@RequestParam String url) {
        return problemService.fetchProblemInfo(url);
    }
}
