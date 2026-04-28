package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 人员实体类
 * 用于映射数据库中的person表
 * 支持人员(PERSON)和球员(PLAYER)两种类型
 *
 * @author system
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Person extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 所属部门ID
     */
    private Long orgId;

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
}
