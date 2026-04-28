package com.football.mapper;

import com.football.dto.StadiumQueryDTO;
import com.football.entity.Stadium;
import com.football.vo.StadiumVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StadiumMapper {

    StadiumVO selectById(@Param("id") Long id);

    List<StadiumVO> selectList(@Param("query") StadiumQueryDTO query);

    long selectCount(@Param("query") StadiumQueryDTO query);

    int insert(Stadium stadium);

    int updateById(Stadium stadium);

    int deleteById(@Param("id") Long id);

    int deleteByIds(@Param("ids") List<Long> ids);

    List<StadiumVO> selectAll();

    List<StadiumVO> selectByStatus(@Param("status") Integer status);

    StadiumVO selectByName(@Param("stadiumName") String stadiumName, @Param("excludeId") Long excludeId);
}
