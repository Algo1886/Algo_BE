package com.teamalgo.algo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teamalgo.algo.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String avatarUrl;
    private LocalDate createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer streak;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt().toLocalDate())
                .build();
    }

    public static UserResponse from(User user, int streak) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt().toLocalDate())
                .streak(streak)
                .build();
    }

}
