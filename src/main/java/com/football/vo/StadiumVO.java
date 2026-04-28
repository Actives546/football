package com.football.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StadiumVO {

    private Long id;

    private String stadiumName;

    private String address;

    private Integer capacity;

    private String contactPhone;

    private String photoUrl;

    private Integer status;

    private String statusText;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
