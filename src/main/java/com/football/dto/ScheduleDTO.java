package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ScheduleDTO {

    private Long id;

    @NotNull(message = "所属赛季不能为空")
    private Long seasonId;

    @NotBlank(message = "赛程名称不能为空")
    private String scheduleName;

    @NotBlank(message = "赛程状态不能为空")
    private String status;

    private String location;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
}
