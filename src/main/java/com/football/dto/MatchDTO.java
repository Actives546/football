package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class MatchDTO {

    private Long id;

    @NotBlank(message = "赛事名称不能为空")
    private String matchName;

    @NotBlank(message = "赛事类型不能为空")
    private String matchType;

    @NotBlank(message = "赛事状态不能为空")
    private String status;

    @NotNull(message = "比赛开始时间不能为空")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    private String coverImage;
}
