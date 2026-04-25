package com.football.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // JWT令牌
    private String token;

    // 用户ID
    private Long userId;

    // 用户名
    private String username;

    // 手机号
    private String phone;

    // 昵称
    private String nickname;

    // 头像
    private String avatar;
}
