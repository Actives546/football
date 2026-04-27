package com.football.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PersonVO {

    private Long id;

    private Long orgId;

    private String orgName;

    private String personType;

    private String personName;

    private String phone;

    private String email;

    private Integer gender;

    private LocalDate birthday;

    private String idCard;

    private String address;

    private String avatar;

    private String remark;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
