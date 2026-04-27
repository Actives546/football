package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MatchDTO {

    private Long id;

    @NotBlank(message = "赛事名称不能为空")
    private String matchName;

    @NotBlank(message = "赛事类型不能为空")
    private String matchType;

    @NotBlank(message = "赛事状态不能为空")
    private String status;

    private String description;

    private String coverImage;
}
