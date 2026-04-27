package com.football.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SeasonVO {

    private Long id;

    private Long matchId;

    private String matchName;

    private String seasonName;

    private String seasonYear;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
