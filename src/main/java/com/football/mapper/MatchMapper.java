package com.football.mapper;

import com.football.dto.MatchQueryDTO;
import com.football.entity.Match;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchMapper {

    Match selectById(@Param("id") Long id);

    List<Match> selectList(@Param("query") MatchQueryDTO query);

    long selectCount(@Param("query") MatchQueryDTO query);

    int insert(Match match);

    int updateById(Match match);

    int deleteById(@Param("id") Long id);

    int deleteByIds(@Param("ids") List<Long> ids);
}
