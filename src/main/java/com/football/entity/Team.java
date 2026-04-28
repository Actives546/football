package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 球队实体类
 * 
 * @author system
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Team extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 球队简介
     */
    private String description;
}
