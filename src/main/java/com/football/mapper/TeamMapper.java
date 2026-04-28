package com.football.mapper;

import com.football.dto.TeamQueryDTO;
import com.football.entity.Team;
import com.football.vo.TeamVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 球队数据访问层接口
 * 
 * @author system
 * @version 1.0.0
 */
@Mapper
public interface TeamMapper {

    /**
     * 根据ID查询球队详情
     * 
     * @param id 球队ID
     * @return 球队视图对象
     */
    TeamVO selectById(@Param("id") Long id);

    /**
     * 分页查询球队列表
     * 
     * @param query 查询条件
     * @return 球队列表
     */
    List<TeamVO> selectList(@Param("query") TeamQueryDTO query);

    /**
     * 查询符合条件的总记录数
     * 
     * @param query 查询条件
     * @return 总记录数
     */
    long selectCount(@Param("query") TeamQueryDTO query);

    /**
     * 插入球队
     * 
     * @param team 球队实体
     * @return 影响行数
     */
    int insert(Team team);

    /**
     * 根据ID更新球队
     * 
     * @param team 球队实体
     * @return 影响行数
     */
    int updateById(Team team);

    /**
     * 根据ID逻辑删除球队
     * 
     * @param id 球队ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除球队
     * 
     * @param ids 球队ID列表
     * @return 影响行数
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 查询所有球队列表（不分页）
     * 
     * @return 球队列表
     */
    List<TeamVO> selectAll();

    /**
     * 根据球队名称查询（用于校验名称唯一性）
     * 
     * @param teamName 球队名称
     * @param excludeId 排除的ID（编辑时使用）
     * @return 球队视图对象
     */
    TeamVO selectByName(@Param("teamName") String teamName, @Param("excludeId") Long excludeId);
}
