package com.teamalgo.algo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkCreateRequest {

    @NotNull(message = "Record ID cannot be null")
    private Long recordId;
}
