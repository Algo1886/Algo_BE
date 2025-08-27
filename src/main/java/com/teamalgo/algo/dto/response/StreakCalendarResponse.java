package com.teamalgo.algo.dto.response;

import com.teamalgo.algo.dto.StreakRecordDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class StreakCalendarResponse {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<StreakRecordDTO> records;
    private int totalCount;
}
