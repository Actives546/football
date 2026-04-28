package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class Schedule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long seasonId;

    private String scheduleName;

    private String status;

    private String location;

    private LocalDateTime startTime;
}
