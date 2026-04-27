package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class Person extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long orgId;

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
}
