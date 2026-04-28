package com.football.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员信息视图对象
 * 用于返回给前端的人员信息展示
 *
 * @author system
 * @version 1.0.0
 */
@Data
public class PersonVO {

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
     * 备注
     */
    private String remark;

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
}
