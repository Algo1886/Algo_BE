package com.teamalgo.algo.global.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ProblemFetcher {

    private final RestTemplate restTemplate = new RestTemplate();

    // 백준
    public String fetchBaekjoonTitle(Long problemId) {
        String url = "https://solved.ac/api/v3/problem/show?problemId=" + problemId;
        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode body = response.getBody();
                if (body != null && body.has("titleKo")) {
                    return body.get("titleKo").asText();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch Baekjoon title for problemId={}", problemId, e);
        }
        return null;
    }

    // 프로그래머스
    public String fetchProgrammersTitle(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            String rawTitle = doc.title()
                    .replace(" | 프로그래머스 스쿨", "")
                    .trim();
            String title = rawTitle;

            title = title.replaceFirst("^코딩테스트 연습 - ", "");
            title = title.replaceAll("\\[PCCP 모의고사 \\d+\\]\\s*", "");
            title = title.replaceFirst("^PCCP 모의고사 \\d+회 - \\d+번 / ", "");
            return title.trim();
        } catch (Exception e) {
            log.warn("Failed to fetch Programmers title for url={}", url, e);
            return null;
        }
    }
}
