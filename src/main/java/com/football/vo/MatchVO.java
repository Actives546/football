package com.football.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchVO {

    private Long id;

    private String matchName;

    private String matchType;

    private String status;

    private String description;

    private String coverImage;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
