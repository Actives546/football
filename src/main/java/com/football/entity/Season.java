package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class Season extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long matchId;

    private String seasonName;

    private String seasonYear;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private Integer totalRounds;

    private Integer currentRound;
}
