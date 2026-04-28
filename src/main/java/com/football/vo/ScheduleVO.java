package com.football.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleVO {

    private Long id;

    private Long seasonId;

    private String seasonName;

    private Long matchId;

    private String matchName;

    private String scheduleName;

    private String status;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
