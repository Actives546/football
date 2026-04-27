package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Match extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String matchName;

    private String matchType;

    private String description;

    private String coverImage;
}
