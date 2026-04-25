package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 用户名，用于用户名密码登录
    private String username;

    // 手机号，用于手机号验证码登录
    private String phone;

    // 密码，加密存储
    private String password;

    // 昵称
    private String nickname;

    // 头像URL
    private String avatar;

    // 邮箱
    private String email;

    // 用户状态：0-禁用，1-正常
    private Integer status;

    // 最后登录时间
    private LocalDateTime lastLoginTime;
}
