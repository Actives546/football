package com.football.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrganizationVO {

    private Long id;

    private String orgName;

    private String orgType;

    private String orgShortName;

    private String description;

    private Long parentId;

    private String parentName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
