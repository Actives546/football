package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StadiumDTO {

    private Long id;

    @NotBlank(message = "场地名称不能为空")
    private String stadiumName;

    private String address;

    private Integer capacity;

    private String contactPhone;

    private String photoUrl;

    @NotNull(message = "状态不能为空")
    private Integer status = 1;

    private String remark;
}
