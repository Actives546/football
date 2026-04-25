package com.football.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchQueryDTO {

    private String matchName;

    private String league;

    private String homeTeam;

    private String awayTeam;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
