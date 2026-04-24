package com.football.mapper;

import com.football.dto.SeasonQueryDTO;
import com.football.entity.Season;
import com.football.vo.SeasonVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeasonMapper {

    SeasonVO selectById(@Param("id") Long id);

    List<SeasonVO> selectList(@Param("query") SeasonQueryDTO query);

    long selectCount(@Param("query") SeasonQueryDTO query);

    int insert(Season season);

    int updateById(Season season);

    int deleteById(@Param("id") Long id);

    int deleteByIds(@Param("ids") List<Long> ids);

    List<SeasonVO> selectByMatchId(@Param("matchId") Long matchId);
}
