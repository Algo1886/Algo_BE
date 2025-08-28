package com.teamalgo.algo.dto.response;

import com.teamalgo.algo.dto.CoreIdeaDTO;
import com.teamalgo.algo.dto.RecordDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class DashboardResponse {

    private int recordCount;
    private int streakDays;
    private double successRate;
    private int bookmarkCount;
    private List<RecordDTO> recommendations;
    private List<CoreIdeaDTO> recentIdeas;

}
