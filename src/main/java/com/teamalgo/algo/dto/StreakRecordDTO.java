package com.teamalgo.algo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class StreakRecordDTO {
    private LocalDate date;
    private int count;
}