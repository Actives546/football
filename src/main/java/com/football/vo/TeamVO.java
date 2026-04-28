package com.football.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 球队视图对象
 * 
 * @author system
 * @version 1.0.0
 */
@Data
public class TeamVO {

    /**
     * 球队ID
     */
    private Long id;

    /**
     * 球队名称
     */
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
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 状态文本描述
     */
    private String statusText;

    /**
     * 球队简介
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
