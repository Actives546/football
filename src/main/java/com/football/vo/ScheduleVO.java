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

    private Long stadiumId;

    private String stadiumName;

    private String stadiumAddress;

    private Integer stadiumCapacity;

    private String stadiumContactPhone;

    private String stadiumPhotoUrl;

    private Integer stadiumStatus;

    private String stadiumRemark;

    private String scheduleName;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
