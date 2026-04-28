package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Stadium extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String stadiumName;

    private String address;

    private Integer capacity;

    private String contactPhone;

    private String photoUrl;

    private Integer status;

    private String remark;
}
