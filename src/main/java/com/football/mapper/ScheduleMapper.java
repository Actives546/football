package com.football.mapper;

import com.football.dto.ScheduleQueryDTO;
import com.football.entity.Schedule;
import com.football.vo.ScheduleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleMapper {

    ScheduleVO selectById(@Param("id") Long id);

    List<ScheduleVO> selectList(@Param("query") ScheduleQueryDTO query);

    long selectCount(@Param("query") ScheduleQueryDTO query);

    int insert(Schedule schedule);

    int updateById(Schedule schedule);

    int deleteById(@Param("id") Long id);

    int deleteByIds(@Param("ids") List<Long> ids);

    List<ScheduleVO> selectBySeasonId(@Param("seasonId") Long seasonId);
}
