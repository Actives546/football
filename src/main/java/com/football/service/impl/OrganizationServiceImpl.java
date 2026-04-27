package com.football.service.impl;

import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.dto.OrganizationDTO;
import com.football.dto.OrganizationQueryDTO;
import com.football.entity.Organization;
import com.football.mapper.OrganizationMapper;
import com.football.service.OrganizationService;
import com.football.vo.OrganizationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationMapper organizationMapper;

    @Override
    public Result<OrganizationVO> getById(Long id) {
        if (id == null) {
            throw new BusinessException("机构ID不能为空");
        }
        OrganizationVO organizationVO = organizationMapper.selectById(id);
        if (organizationVO == null) {
            throw new BusinessException("机构不存在");
        }
        return Result.success("查询成功", organizationVO);
    }

    @Override
    public Result<Map<String, Object>> getPage(OrganizationQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(10);
        }

        List<OrganizationVO> organizationList = organizationMapper.selectList(queryDTO);
        long total = organizationMapper.selectCount(queryDTO);

        Map<String, Object> result = new HashMap<>();
        result.put("list", organizationList);
        result.put("total", total);
        result.put("pageNum", queryDTO.getPageNum());
        result.put("pageSize", queryDTO.getPageSize());

        return Result.success("查询成功", result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(OrganizationDTO organizationDTO) {
        validateOrganizationDTO(organizationDTO);
        validateParentExist(organizationDTO.getParentId());

        Organization organization = convertToEntity(organizationDTO);

        int rows = organizationMapper.insert(organization);
        log.info("新增机构成功，机构ID：{}，机构名称：{}", organization.getId(), organization.getOrgName());

        return Result.success("新增成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(OrganizationDTO organizationDTO) {
        if (organizationDTO.getId() == null) {
            throw new BusinessException("机构ID不能为空");
        }

        OrganizationVO existOrganization = organizationMapper.selectById(organizationDTO.getId());
        if (existOrganization == null) {
            throw new BusinessException("机构不存在");
        }

        if (organizationDTO.getParentId() != null && organizationDTO.getParentId().equals(organizationDTO.getId())) {
            throw new BusinessException("父机构不能是自身");
        }

        validateOrganizationDTO(organizationDTO);
        validateParentExist(organizationDTO.getParentId());

        Organization organization = convertToEntity(organizationDTO);

        int rows = organizationMapper.updateById(organization);
        log.info("更新机构成功，机构ID：{}，机构名称：{}", organization.getId(), organization.getOrgName());

        return Result.success("更新成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        if (id == null) {
            throw new BusinessException("机构ID不能为空");
        }

        OrganizationVO organization = organizationMapper.selectById(id);
        if (organization == null) {
            throw new BusinessException("机构不存在");
        }

        int childrenCount = organizationMapper.selectChildrenCount(id);
        if (childrenCount > 0) {
            throw new BusinessException("该机构下存在子机构，无法删除");
        }

        int rows = organizationMapper.deleteById(id);
        log.info("删除机构成功，机构ID：{}，机构名称：{}", id, organization.getOrgName());

        return Result.success("删除成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的机构");
        }

        for (Long id : ids) {
            int childrenCount = organizationMapper.selectChildrenCount(id);
            if (childrenCount > 0) {
                throw new BusinessException("存在包含子机构的机构，无法批量删除");
            }
        }

        int rows = organizationMapper.deleteByIds(ids);
        log.info("批量删除机构成功，删除数量：{}", rows);

        return Result.success("批量删除成功", rows > 0);
    }

    @Override
    public Result<List<OrganizationVO>> getChildren(Long parentId) {
        if (parentId == null) {
            parentId = 0L;
        }
        List<OrganizationVO> list = organizationMapper.selectChildren(parentId);
        return Result.success("查询成功", list);
    }

    @Override
    public Result<List<OrganizationVO>> getAll() {
        List<OrganizationVO> list = organizationMapper.selectAll();
        return Result.success("查询成功", list);
    }

    @Override
    public Result<Map<String, Object>> getTree() {
        List<OrganizationVO> allList = organizationMapper.selectAll();
        
        Map<Long, List<OrganizationVO>> parentMap = allList.stream()
            .collect(Collectors.groupingBy(org -> org.getParentId() == null ? 0L : org.getParentId()));
        
        List<Map<String, Object>> tree = buildTree(0L, parentMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("tree", tree);
        result.put("list", allList);
        
        return Result.success("查询成功", result);
    }

    private List<Map<String, Object>> buildTree(Long parentId, Map<Long, List<OrganizationVO>> parentMap) {
        List<Map<String, Object>> tree = new ArrayList<>();
        List<OrganizationVO> children = parentMap.get(parentId);
        
        if (!CollectionUtils.isEmpty(children)) {
            for (OrganizationVO org : children) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", org.getId());
                node.put("label", org.getOrgName());
                node.put("value", org.getId());
                node.put("orgType", org.getOrgType());
                node.put("orgShortName", org.getOrgShortName());
                node.put("children", buildTree(org.getId(), parentMap));
                tree.add(node);
            }
        }
        
        return tree;
    }

    private void validateOrganizationDTO(OrganizationDTO organizationDTO) {
        if (!StringUtils.hasText(organizationDTO.getOrgName())) {
            throw new BusinessException("机构名称不能为空");
        }
    }

    private void validateParentExist(Long parentId) {
        if (parentId != null && parentId > 0) {
            OrganizationVO parent = organizationMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException("父机构不存在");
            }
        }
    }

    private Organization convertToEntity(OrganizationDTO dto) {
        Organization organization = new Organization();
        BeanUtils.copyProperties(dto, organization);
        return organization;
    }
}
