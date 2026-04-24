package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MatchDTO {

    private Long id;

    @NotBlank(message = "赛事名称不能为空")
    private String matchName;

    @NotBlank(message = "联赛名称不能为空")
    private String league;

    @NotBlank(message = "主队名称不能为空")
    private String homeTeam;

    @NotBlank(message = "客队名称不能为空")
    private String awayTeam;

    @NotNull(message = "比赛时间不能为空")
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
