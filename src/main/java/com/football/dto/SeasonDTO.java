package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class SeasonDTO {

    private Long id;

    @NotNull(message = "所属赛事不能为空")
    private Long matchId;

    @NotBlank(message = "赛季名称不能为空")
    private String seasonName;

    private String seasonYear;

    @NotBlank(message = "赛季状态不能为空")
    private String status;

    @NotNull(message = "赛季开始日期不能为空")
    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private Integer totalRounds;

    private Integer currentRound;
}
