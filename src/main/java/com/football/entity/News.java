package com.football.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class News extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    private String coverImage;

    private String category;

    private String content;

    private String status;

    private Long viewCount;
}
