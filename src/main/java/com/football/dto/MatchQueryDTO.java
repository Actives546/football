package com.football.dto;

import lombok.Data;

@Data
public class MatchQueryDTO {

    private static final int MAX_PAGE_SIZE = 50;

    private String matchName;

    private String matchType;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    public Integer getPageSize() {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    public Integer getOffset() {
        if (pageNum == null || pageNum < 1) {
            return 0;
        }
        return (pageNum - 1) * getPageSize();
    }
}
