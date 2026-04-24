package com.football.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchQueryDTO {

    private String matchName;

    private String matchType;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    public Integer getOffset() {
        if (pageNum == null || pageSize == null || pageNum < 1 || pageSize < 1) {
            return 0;
        }
        return (pageNum - 1) * pageSize;
    }
}
