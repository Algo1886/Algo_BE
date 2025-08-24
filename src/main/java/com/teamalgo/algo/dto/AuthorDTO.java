package com.teamalgo.algo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthorDTO {
    private Long id;          // 작성자 ID
    private String username;  // 사용자 이름 또는 handle
    private String avatarUrl; // 프로필 이미지 URL (선택적)
}
