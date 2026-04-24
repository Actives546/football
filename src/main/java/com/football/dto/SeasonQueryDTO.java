package com.football.dto;

import lombok.Data;

@Data
public class SeasonQueryDTO {

    private Long matchId;

    private String seasonName;

    private String seasonYear;

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
