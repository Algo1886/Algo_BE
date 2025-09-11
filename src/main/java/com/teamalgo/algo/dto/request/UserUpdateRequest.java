package com.teamalgo.algo.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Size(min = 2, max = 10, message = "닉네임은 2~10자 이내여야 합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$",
            message = "닉네임은 한글, 영어, 숫자만 사용할 수 있습니다.")
    private String username;
    private String avatarUrl;

}
