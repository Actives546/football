package com.football.dto;

import lombok.Data;

@Data
public class StadiumQueryDTO {

    private Integer pageNum;

    private Integer pageSize;

    private String stadiumName;

    private String address;

    private Integer status;

    public Integer getOffset() {
        if (pageNum == null || pageSize == null) {
            return null;
        }
        return (pageNum - 1) * pageSize;
    }
}
