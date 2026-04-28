package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 球队数据传输对象
 * 
 * @author system
 * @version 1.0.0
 */
@Data
public class TeamDTO {

    /**
     * 球队ID（编辑时必填）
     */
    private Long id;

    /**
     * 球队名称（必填）
     */
    @NotBlank(message = "球队名称不能为空")
    private String teamName;

    /**
     * 球队Logo URL
     */
    private String logoUrl;

    /**
     * 地区
     */
    private String region;

    /**
     * 成立时间
     */
    private LocalDate foundingDate;

    /**
     * 主场
     */
    private String homeStadium;

    /**
     * 主教练
     */
    private String headCoach;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 状态：0-禁用，1-启用（默认1）
     */
    @NotNull(message = "状态不能为空")
    private Integer status = 1;

    /**
     * 球队简介
     */
    private String description;
}
