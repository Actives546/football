package com.football.dto;

import lombok.Data;

/**
 * 人员查询条件数据传输对象
 * 用于接收前端传递的分页查询条件
 *
 * @author system
 * @version 1.0.0
 */
@Data
public class PersonQueryDTO {

    /**
     * 所属部门ID
     */
    private Long orgId;

    /**
     * 人员类型：PERSON-普通人员，PLAYER-球员
     */
    private String personType;

    /**
     * 人员姓名（模糊查询）
     */
    private String personName;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 当前页码，默认值1
     */
    private Integer pageNum = 1;

    /**
     * 每页大小，默认值10
     */
    private Integer pageSize = 10;

    /**
     * 计算分页偏移量
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
