package com.football.dto;

import lombok.Data;

/**
 * 球队查询数据传输对象
 * 
 * @author system
 * @version 1.0.0
 */
@Data
public class TeamQueryDTO {

    /**
     * 球队名称（模糊查询）
     */
    private String teamName;

    /**
     * 地区
     */
    private String region;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 获取偏移量
     * 
     * @return 偏移量
     */
    public Integer getOffset() {
        if (pageNum == null || pageSize == null || pageNum < 1 || pageSize < 1) {
            return 0;
        }
        return (pageNum - 1) * pageSize;
    }
}
