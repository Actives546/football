package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Organization extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String orgName;

    private String orgType;

    private String orgShortName;

    private String description;

    private Long parentId;
}
