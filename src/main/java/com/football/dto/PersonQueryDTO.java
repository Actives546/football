package com.football.dto;

import lombok.Data;

@Data
public class PersonQueryDTO {

    private Long orgId;

    private String personType;

    private String personName;

    private String phone;

    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    public Integer getOffset() {
        if (pageNum == null || pageSize == null || pageNum < 1 || pageSize < 1) {
            return 0;
        }
        return (pageNum - 1) * pageSize;
    }
}
