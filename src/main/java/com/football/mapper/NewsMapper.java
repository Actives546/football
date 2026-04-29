package com.football.mapper;

import com.football.dto.NewsQueryDTO;
import com.football.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewsMapper {

    News selectById(@Param("id") Long id);

    List<News> selectList(@Param("query") NewsQueryDTO query);

    long selectCount(@Param("query") NewsQueryDTO query);

    int insert(News news);

    int updateById(News news);

    int deleteById(@Param("id") Long id);

    int deleteByIds(@Param("ids") List<Long> ids);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") String status);
}
