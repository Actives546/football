package com.football.mapper;

import com.football.dto.OrganizationQueryDTO;
import com.football.entity.Organization;
import com.football.vo.OrganizationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrganizationMapper {

    OrganizationVO selectById(@Param("id") Long id);

    List<OrganizationVO> selectList(@Param("query") OrganizationQueryDTO query);

    long selectCount(@Param("query") OrganizationQueryDTO query);

    int insert(Organization organization);

    int updateById(Organization organization);

    int deleteById(@Param("id") Long id);

    int deleteByIds(@Param("ids") List<Long> ids);

    List<OrganizationVO> selectChildren(@Param("parentId") Long parentId);

    List<OrganizationVO> selectAll();

    int selectChildrenCount(@Param("parentId") Long parentId);
}
