package com.teamalgo.algo.global.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProblemSourceDetector {

    public static String detectSource(String url) {
        if (url.contains("acmicpc.net")) return "boj";
        if (url.contains("programmers.co.kr")) return "programmers";
        if (url.contains("leetcode.com")) return "leetcode";
        if (url.contains("codeforces.com")) return "codeforces";
        return "unknown";
    }

    public static String extractExternalId(String url, String source) {
        try {
            URI uri = new URI(url);

            switch (source) {
                case "boj": {
                    // https://www.acmicpc.net/problem/1000
                    Pattern pattern = Pattern.compile("/problem/(\\d+)");
                    Matcher matcher = pattern.matcher(uri.getPath());
                    if (matcher.find()) return matcher.group(1);
                    break;
                }
                case "programmers": {
                    // https://school.programmers.co.kr/learn/courses/30/lessons/42576
                    Pattern pattern = Pattern.compile("/lessons/(\\d+)");
                    Matcher matcher = pattern.matcher(uri.getPath());
                    if (matcher.find()) return matcher.group(1);
                    break;
                }
                case "leetcode": {
                    // https://leetcode.com/problems/two-sum/
                    Pattern pattern = Pattern.compile("/problems/([a-z0-9-]+)/?");
                    Matcher matcher = pattern.matcher(uri.getPath());
                    if (matcher.find()) return matcher.group(1);
                    break;
                }
                case "codeforces": {
                    // https://codeforces.com/problemset/problem/4/A
                    Pattern pattern = Pattern.compile("/problemset/problem/(\\d+)/(\\w+)");
                    Matcher matcher = pattern.matcher(uri.getPath());
                    if (matcher.find()) return matcher.group(1) + matcher.group(2);
                    break;
                }
                default:
                    return "unknown";
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format: " + url, e);
        }
        return "unknown";
    }
}
