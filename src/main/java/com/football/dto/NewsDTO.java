package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NewsDTO {

    private Long id;

    @NotBlank(message = "新闻标题不能为空")
    private String title;

    private String coverImage;

    @NotBlank(message = "新闻分类不能为空")
    private String category;

    private String content;

    @NotBlank(message = "发布状态不能为空")
    private String status;
}
