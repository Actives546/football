package com.football.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员详细信息视图对象
 * 用于返回给前端的人员详细信息展示
 * 包含人员基本信息和详细补充信息
 *
 * @author system
 * @version 1.0.0
 */
@Data
public class PersonDetailVO {

    /**
     * 人员ID
     */
    private Long id;

    /**
     * 所属部门ID
     */
    private Long orgId;

    /**
     * 所属部门名称
     */
    private String orgName;

    /**
     * 人员类型：PERSON-普通人员，PLAYER-球员
     */
    private String personType;

    /**
     * 人员姓名
     */
    private String personName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 地址
     */
    private String address;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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
    private String detailRemark;
}
