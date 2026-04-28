package com.football.service;

import com.football.common.Result;
import com.football.dto.TeamDTO;
import com.football.dto.TeamQueryDTO;
import com.football.vo.TeamVO;

import java.util.List;
import java.util.Map;

/**
 * 球队管理业务接口
 * 
 * @author system
 * @version 1.0.0
 */
public interface TeamService {

    /**
     * 根据ID查询球队详情
     * 
     * @param id 球队ID
     * @return 球队详情视图对象
     */
    Result<TeamVO> getById(Long id);

    /**
     * 分页查询球队列表
     * 
     * @param queryDTO 查询条件DTO
     * @return 分页结果，包含列表、总数、页码、每页数量
     */
    Result<Map<String, Object>> getPage(TeamQueryDTO queryDTO);

    /**
     * 新增球队
     * 
     * @param teamDTO 球队数据DTO
     * @return 新增是否成功
     */
    Result<Boolean> add(TeamDTO teamDTO);

    /**
     * 更新球队信息
     * 
     * @param teamDTO 球队数据DTO
     * @return 更新是否成功
     */
    Result<Boolean> update(TeamDTO teamDTO);

    /**
     * 根据ID删除球队（逻辑删除）
     * 
     * @param id 球队ID
     * @return 删除是否成功
     */
    Result<Boolean> delete(Long id);

    /**
     * 批量删除球队（逻辑删除）
     * 
     * @param ids 球队ID列表
     * @return 删除是否成功
     */
    Result<Boolean> deleteBatch(List<Long> ids);

    /**
     * 查询所有球队列表（不分页）
     * 
     * @return 所有球队列表
     */
    Result<List<TeamVO>> getAll();

    /**
     * 导出球队数据
     * 
     * @param queryDTO 查询条件DTO
     * @return 导出的球队列表
     */
    Result<List<TeamVO>> export(TeamQueryDTO queryDTO);

    /**
     * 批量更新球队状态
     * 
     * @param ids 球队ID列表
     * @param status 状态值
     * @return 更新是否成功
     */
    Result<Boolean> updateStatusBatch(List<Long> ids, Integer status);
}
