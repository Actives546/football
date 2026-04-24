package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class Match extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String matchName;

    private String matchType;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    private String coverImage;
}
