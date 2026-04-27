package com.football.mapper;

import com.football.dto.PersonQueryDTO;
import com.football.entity.Person;
import com.football.vo.PersonVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PersonMapper {

    PersonVO selectById(@Param("id") Long id);

    List<PersonVO> selectList(@Param("query") PersonQueryDTO query);

    long selectCount(@Param("query") PersonQueryDTO query);

    int insert(Person person);

    int updateById(Person person);

    int deleteById(@Param("id") Long id);

    int deleteByIds(@Param("ids") List<Long> ids);

    List<PersonVO> selectByOrgId(@Param("orgId") Long orgId);

    int selectByOrgIdCount(@Param("orgId") Long orgId);
}
