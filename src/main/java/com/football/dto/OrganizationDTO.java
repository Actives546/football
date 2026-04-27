package com.football.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OrganizationDTO {

    private Long id;

    @NotBlank(message = "机构名称不能为空")
    private String orgName;

    private String orgType;

    private String orgShortName;

    private String description;

    private Long parentId;
}
