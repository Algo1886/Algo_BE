package com.teamalgo.algo.global.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProblemSourceDetector {

    public static String detectSource(String url) {
        if (url.contains("acmicpc.net")) return "백준";
        if (url.contains("programmers.co.kr")) return "프로그래머스";
        if (url.contains("codeup.kr")) return "코드업";
        if (url.contains("codeforces.com")) return "코드포스";
        if (url.contains("swexpertacademy.com")) return "SW Expert Academy";
        if (url.contains("leetcode.com")) return "리트코드";
        return "unknown";
    }

    public static Long extractNumericId(String url, String source) {
        try {
            URI uri = new URI(url);

            switch (source) {
                case "백준": {
                    Pattern p = Pattern.compile("/problem/(\\d+)");
                    Matcher m = p.matcher(uri.getPath());
                    if (m.find()) return Long.valueOf(m.group(1));
                    break;
                }
                case "프로그래머스": {
                    Pattern p = Pattern.compile("/lessons/(\\d+)");
                    Matcher m = p.matcher(uri.getPath());
                    if (m.find()) return Long.valueOf(m.group(1));
                    break;
                }
                case "코드업": {
                    Pattern p = Pattern.compile("id=(\\d+)");
                    Matcher m = p.matcher(uri.getQuery());
                    if (m.find()) return Long.valueOf(m.group(1));
                    break;
                }
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format: " + url, e);
        }
        return null;
    }

    public static String extractSlugId(String url, String source) {
        try {
            URI uri = new URI(url);

            switch (source) {
                case "리트코드": {
                    Pattern p = Pattern.compile("/problems/([a-z0-9-]+)/?");
                    Matcher m = p.matcher(uri.getPath());
                    if (m.find()) return m.group(1);
                    break;
                }
                case "SW Expert Academy": {
                    Pattern p = Pattern.compile("contestProbId=([A-Za-z0-9]+)");
                    Matcher m = p.matcher(uri.getQuery());
                    if (m.find()) return m.group(1);
                    break;
                }
                case "코드포스": {
                    Pattern p = Pattern.compile("/problemset/problem/(\\d+)/(\\w+)");
                    Matcher m = p.matcher(uri.getPath());
                    if (m.find()) {
                        return m.group(1) + m.group(2);
                    }
                    break;
                }
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format: " + url, e);
        }
        return null;
    }
}
