package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 登录类型：phone-手机号验证码登录，password-用户名密码登录
    @NotBlank(message = "登录类型不能为空")
    private String loginType;

    // 用户名（密码登录时使用）
    private String username;

    // 手机号（手机号登录时使用）
    private String phone;

    // 密码（密码登录时使用）
    private String password;

    // 验证码（手机号登录时使用）
    private String code;
}
