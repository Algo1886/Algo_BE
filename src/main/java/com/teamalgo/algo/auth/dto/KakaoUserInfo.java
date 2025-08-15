package com.teamalgo.algo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoUserInfo {
    private String providerId;
    private String nickname;
}
