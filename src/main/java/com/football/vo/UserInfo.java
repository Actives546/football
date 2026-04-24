package com.football.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String phone;
    private String nickname;
    private String avatar;
}
