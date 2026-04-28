package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 人员信息数据传输对象
 * 用于接收前端传递的人员信息
 * 支持人员(PERSON)和球员(PLAYER)两种类型
 *
 * @author system
 * @version 1.0.0
 */
@Data
public class PersonDTO {

    /**
     * 人员ID，新增时为空，修改时必填
     */
    private Long id;

    /**
     * 所属部门ID
     */
    @NotNull(message = "所属部门不能为空")
    private Long orgId;

    /**
     * 人员类型：PERSON-普通人员，PLAYER-球员
     */
    @NotBlank(message = "人员类型不能为空")
    private String personType;

    /**
     * 人员姓名
     */
    @NotBlank(message = "人员姓名不能为空")
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
