package com.football.service;

import com.football.common.Result;
import com.football.dto.OrganizationDTO;
import com.football.dto.OrganizationQueryDTO;
import com.football.vo.OrganizationVO;

import java.util.List;
import java.util.Map;

public interface OrganizationService {

    Result<OrganizationVO> getById(Long id);

    Result<Map<String, Object>> getPage(OrganizationQueryDTO queryDTO);

    Result<Boolean> add(OrganizationDTO organizationDTO);

    Result<Boolean> update(OrganizationDTO organizationDTO);

    Result<Boolean> delete(Long id);

    Result<Boolean> deleteBatch(List<Long> ids);

    Result<List<OrganizationVO>> getChildren(Long parentId);

    Result<List<OrganizationVO>> getAll();

    Result<Map<String, Object>> getTree();
}
