package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 人员详细信息实体类
 * 用于映射数据库中的person_detail表
 * 存储人员的详细补充信息，区分普通人员和球员
 *
 * @author system
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 场上位置（球员专用）：前锋、中场、后卫、守门员等
     */
    private String fieldPosition;

    /**
     * 身高（cm，球员专用）
     */
    private Integer height;

    /**
     * 体重（kg，球员专用）
     */
    private Integer weight;

    /**
     * 惯用脚（球员专用）：LEFT-左脚，RIGHT-右脚，BOTH-双脚
     */
    private String preferredFoot;

    /**
     * 国籍（球员专用）
     */
    private String nationality;

    /**
     * 入队日期（球员专用）
     */
    private LocalDate teamJoinDate;

    /**
     * 合同到期日期（球员专用）
     */
    private LocalDate contractEndDate;

    /**
     * 身价（万元，球员专用）
     */
    private BigDecimal marketValue;

    /**
     * 技术特点描述（球员专用）
     */
    private String technicalFeatures;

    /**
     * 过往经历描述（球员专用）
     */
    private String pastExperience;

    /**
     * 备注（通用）
     */
    private String remark;
}
