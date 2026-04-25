package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class Match extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String matchName;

    private String league;

    private String homeTeam;

    private String awayTeam;

    private LocalDateTime matchTime;

    private String venue;

    private String status;

    private Integer homeScore;

    private Integer awayScore;

    private String referee;

    private Integer audience;

    private BigDecimal ticketPrice;

    private String description;

    private String coverImage;
}
