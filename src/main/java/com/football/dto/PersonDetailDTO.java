package com.football.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 人员详细信息数据传输对象
 * 用于接收前端传递的人员详细信息
 *
 * @author system
 * @version 1.0.0
 */
@Data
public class PersonDetailDTO {

    /**
     * 关联人员ID
     */
    private Long personId;

    /**
     * 职位/岗位（通用）
     */
    private String position;

    /**
     * 入职日期（通用）
     */
    private LocalDate joinDate;

    /**
     * 学历（通用）
     */
    private String education;

    /**
     * 专业（通用）
     */
    private String major;

    /**
     * 毕业院校（通用）
     */
    private String school;

    /**
     * 紧急联系人（通用）
     */
    private String emergencyContact;

    /**
     * 紧急联系电话（通用）
     */
    private String emergencyPhone;

    /**
     * 工作经验描述（通用）
     */
    private String workExperience;

    /**
     * 球衣号码（球员专用）
     */
    private Integer jerseyNumber;

    /**
     * 场上位置（球员专用）
     */
    private String fieldPosition;

    /**
     * 身高（cm）
     */
    private Integer height;

    /**
     * 体重（kg）
     */
    private Integer weight;

    /**
     * 惯用脚：LEFT-左脚，RIGHT-右脚，BOTH-双脚
     */
    private String preferredFoot;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 入队日期
     */
    private LocalDate teamJoinDate;

    /**
     * 合同到期日期
     */
    private LocalDate contractEndDate;

    /**
     * 身价（万元）
     */
    private BigDecimal marketValue;

    /**
     * 技术特点描述
     */
    private String technicalFeatures;

    /**
     * 过往经历描述
     */
    private String pastExperience;

    /**
     * 详细备注
     */
    private String remark;
}
