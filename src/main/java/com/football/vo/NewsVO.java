package com.football.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsVO {

    private Long id;

    private String title;

    private String coverImage;

    private String category;

    private String content;

    private String status;

    private Long viewCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
