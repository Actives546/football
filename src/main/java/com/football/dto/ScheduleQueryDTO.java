package com.football.dto;

import lombok.Data;

@Data
public class ScheduleQueryDTO {

    private Long seasonId;

    private Long stadiumId;

    private String scheduleName;

    private String status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    public Integer getOffset() {
        if (pageNum == null || pageSize == null || pageNum < 1 || pageSize < 1) {
            return 0;
        }
        return (pageNum - 1) * pageSize;
    }
}
