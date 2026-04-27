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

/**
 * 机构管理业务实现类
 * 
 * 1. 实现机构的增删改查等核心业务逻辑
 * 2. 处理机构的层级关系校验（父机构存在性、不能设自身为父机构等）
 * 3. 提供分页查询、树形结构查询、批量删除等扩展功能
 * 4. 实现逻辑删除，确保数据可追溯
 * 
 * @author system
 * @version 1.0.0
 */
@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {

    /** 机构数据访问层 */
    @Autowired
    private OrganizationMapper organizationMapper;

    /**
     * 根据ID查询机构详情
     * 
     * 业务流程：
     * 1. 校验机构ID不能为空
     * 2. 调用Mapper查询机构信息（关联查询父机构名称）
     * 3. 校验查询结果，机构不存在则抛出业务异常
     * 4. 返回成功结果，包含机构详情
     * 
     * @param id 机构ID
     * @return 机构详情视图对象
     * @throws BusinessException 当机构ID为空或机构不存在时抛出
     */
    @Override
    public Result<OrganizationVO> getById(Long id) {
        // 校验机构ID不能为空，为空则抛出业务异常
        if (id == null) {
            throw new BusinessException("机构ID不能为空");
        }
        // 根据ID查询机构详情，同时关联查询父机构名称
        OrganizationVO organizationVO = organizationMapper.selectById(id);
        // 校验机构是否存在，不存在则抛出业务异常
        if (organizationVO == null) {
            throw new BusinessException("机构不存在");
        }
        // 返回成功结果，包含机构详情信息
        return Result.success("查询成功", organizationVO);
    }

    /**
     * 分页查询机构列表
     * 
     * 业务流程：
     * 1. 校验分页参数，设置默认值（pageNum默认1，pageSize默认10）
     * 2. 调用Mapper查询机构列表（支持按机构名称模糊查询、机构类型精确查询）
     * 3. 调用Mapper查询符合条件的总记录数
     * 4. 组装分页结果返回，包含列表、总数、页码、每页数量
     * 
     * @param queryDTO 查询条件DTO
     * @return 分页结果，包含列表、总数、页码、每页数量
     */
    @Override
    public Result<Map<String, Object>> getPage(OrganizationQueryDTO queryDTO) {
        // 校验页码参数，为空或小于等于0时设置默认值为1
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }
        // 校验每页数量参数，为空或小于等于0时设置默认值为10
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(10);
        }

        // 根据查询条件分页查询机构列表
        List<OrganizationVO> organizationList = organizationMapper.selectList(queryDTO);
        // 查询符合条件的总记录数，用于分页
        long total = organizationMapper.selectCount(queryDTO);

        // 组装返回结果，包含列表数据、总记录数、当前页码、每页数量
        Map<String, Object> result = new HashMap<>();
        // 设置机构列表数据
        result.put("list", organizationList);
        // 设置总记录数
        result.put("total", total);
        // 设置当前页码
        result.put("pageNum", queryDTO.getPageNum());
        // 设置每页数量
        result.put("pageSize", queryDTO.getPageSize());

        // 返回成功结果，包含分页数据
        return Result.success("查询成功", result);
    }

    /**
     * 新增机构
     * 
     * 业务流程：
     * 1. 校验机构数据DTO的必填字段（机构名称不能为空）
     * 2. 校验父机构是否存在（如果选择了父机构）
     * 3. 将DTO转换为实体对象
     * 4. 执行插入操作，自动生成ID
     * 5. 记录操作日志
     * 6. 返回新增是否成功
     * 
     * @param organizationDTO 机构数据DTO
     * @return 新增是否成功
     * @throws BusinessException 当数据校验失败或父机构不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(OrganizationDTO organizationDTO) {
        // 校验机构DTO的必填字段（机构名称不能为空）
        validateOrganizationDTO(organizationDTO);
        // 校验父机构是否存在（如果选择了父机构），不选择则默认为顶级机构
        validateParentExist(organizationDTO.getParentId());

        // 将DTO转换为实体对象，准备插入数据库
        Organization organization = convertToEntity(organizationDTO);

        // 执行插入操作，返回影响的行数
        int rows = organizationMapper.insert(organization);
        // 记录操作日志，便于问题排查和审计
        log.info("新增机构成功，机构ID：{}，机构名称：{}", organization.getId(), organization.getOrgName());

        // 返回新增是否成功（影响行数大于0表示成功）
        return Result.success("新增成功", rows > 0);
    }

    /**
     * 更新机构信息
     * 
     * 业务流程：
     * 1. 校验机构ID不能为空
     * 2. 校验机构是否存在（根据ID查询）
     * 3. 校验父机构不能是自身（防止死循环）
     * 4. 校验更新数据的必填字段
     * 5. 校验父机构是否存在
     * 6. 执行更新操作
     * 7. 记录操作日志
     * 8. 返回更新是否成功
     * 
     * @param organizationDTO 机构数据DTO
     * @return 更新是否成功
     * @throws BusinessException 当机构不存在或数据校验失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(OrganizationDTO organizationDTO) {
        // 校验机构ID不能为空，为空则抛出业务异常
        if (organizationDTO.getId() == null) {
            throw new BusinessException("机构ID不能为空");
        }

        // 根据ID查询机构信息，校验机构是否存在
        OrganizationVO existOrganization = organizationMapper.selectById(organizationDTO.getId());
        // 机构不存在则抛出业务异常
        if (existOrganization == null) {
            throw new BusinessException("机构不存在");
        }

        // 校验父机构不能是自身，防止出现无限级别的层级关系
        if (organizationDTO.getParentId() != null && organizationDTO.getParentId().equals(organizationDTO.getId())) {
            throw new BusinessException("父机构不能是自身");
        }

        // 校验机构DTO的必填字段（机构名称不能为空）
        validateOrganizationDTO(organizationDTO);
        // 校验父机构是否存在（如果选择了父机构）
        validateParentExist(organizationDTO.getParentId());

        // 将DTO转换为实体对象，准备更新数据库
        Organization organization = convertToEntity(organizationDTO);

        // 执行更新操作，返回影响的行数
        int rows = organizationMapper.updateById(organization);
        // 记录操作日志，便于问题排查和审计
        log.info("更新机构成功，机构ID：{}，机构名称：{}", organization.getId(), organization.getOrgName());

        // 返回更新是否成功（影响行数大于0表示成功）
        return Result.success("更新成功", rows > 0);
    }

    /**
     * 根据ID删除机构（逻辑删除）
     * 
     * 业务流程：
     * 1. 校验机构ID不能为空
     * 2. 校验机构是否存在
     * 3. 校验该机构下是否存在子机构（存在则不能删除）
     * 4. 执行逻辑删除操作（更新deleted字段为1）
     * 5. 记录删除日志
     * 6. 返回删除是否成功
     * 
     * @param id 机构ID
     * @return 删除是否成功
     * @throws BusinessException 当机构ID为空、机构不存在或存在子机构时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        // 校验机构ID不能为空，为空则抛出业务异常
        if (id == null) {
            throw new BusinessException("机构ID不能为空");
        }

        // 根据ID查询机构信息，校验机构是否存在
        OrganizationVO organization = organizationMapper.selectById(id);
        // 机构不存在则抛出业务异常
        if (organization == null) {
            throw new BusinessException("机构不存在");
        }

        // 查询该机构下的子机构数量，判断是否可以删除
        int childrenCount = organizationMapper.selectChildrenCount(id);
        // 存在子机构时不能删除，防止数据孤立
        if (childrenCount > 0) {
            throw new BusinessException("该机构下存在子机构，无法删除");
        }

        // 执行逻辑删除操作，更新deleted字段为1，同时更新update_time
        int rows = organizationMapper.deleteById(id);
        // 记录操作日志，便于问题排查和审计
        log.info("删除机构成功，机构ID：{}，机构名称：{}", id, organization.getOrgName());

        // 返回删除是否成功（影响行数大于0表示成功）
        return Result.success("删除成功", rows > 0);
    }

    /**
     * 批量删除机构（逻辑删除）
     * 
     * 业务流程：
     * 1. 校验待删除的ID列表不能为空
     * 2. 遍历每个ID，校验是否存在子机构
     * 3. 任一机构存在子机构则抛出异常，终止操作
     * 4. 执行批量逻辑删除操作
     * 5. 记录删除数量日志
     * 6. 返回删除是否成功
     * 
     * @param ids 机构ID列表
     * @return 删除是否成功
     * @throws BusinessException 当ID列表为空或存在包含子机构的机构时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        // 校验待删除的ID列表不能为空，为空则抛出业务异常
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的机构");
        }

        // 遍历每个待删除的机构ID，校验是否存在子机构
        for (Long id : ids) {
            // 查询该机构下的子机构数量
            int childrenCount = organizationMapper.selectChildrenCount(id);
            // 存在子机构时不能删除，抛出业务异常，终止批量操作
            if (childrenCount > 0) {
                throw new BusinessException("存在包含子机构的机构，无法批量删除");
            }
        }

        // 执行批量逻辑删除操作，一次性更新所有符合条件的记录
        int rows = organizationMapper.deleteByIds(ids);
        // 记录操作日志，便于问题排查和审计
        log.info("批量删除机构成功，删除数量：{}", rows);

        // 返回删除是否成功（影响行数大于0表示成功）
        return Result.success("批量删除成功", rows > 0);
    }

    /**
     * 根据父机构ID查询子机构列表
     * 
     * 业务流程：
     * 1. 校验父机构ID，为空则默认查询顶级机构（parentId=0）
     * 2. 调用Mapper查询该父机构下的所有子机构
     * 3. 返回子机构列表（按创建时间降序排列）
     * 
     * @param parentId 父机构ID，null或0表示查询顶级机构
     * @return 该父机构下的子机构列表
     */
    @Override
    public Result<List<OrganizationVO>> getChildren(Long parentId) {
        // 校验父机构ID，为空则设置默认值为0（表示查询顶级机构）
        if (parentId == null) {
            parentId = 0L;
        }
        // 根据父机构ID查询子机构列表
        List<OrganizationVO> list = organizationMapper.selectChildren(parentId);
        // 返回成功结果，包含子机构列表
        return Result.success("查询成功", list);
    }

    /**
     * 查询所有机构列表
     * 
     * 业务流程：
     * 1. 调用Mapper查询所有未删除的机构
     * 2. 返回完整的机构列表（按创建时间降序排列）
     * 
     * @return 所有机构列表
     */
    @Override
    public Result<List<OrganizationVO>> getAll() {
        // 查询所有未删除的机构列表，不进行分页
        List<OrganizationVO> list = organizationMapper.selectAll();
        // 返回成功结果，包含所有机构列表
        return Result.success("查询成功", list);
    }

    /**
     * 获取机构树形结构数据
     * 
     * 业务流程：
     * 1. 查询所有机构列表
     * 2. 将机构列表按parentId分组，构建父机构-子机构映射关系
     * 3. 递归构建树形结构数据
     * 4. 组装结果返回，包含树形结构和原始列表
     * 
     * @return 树形结构数据，包含tree和list两个字段
     */
    @Override
    public Result<Map<String, Object>> getTree() {
        // 查询所有未删除的机构列表，用于构建树形结构
        List<OrganizationVO> allList = organizationMapper.selectAll();
        
        // 使用Stream API将机构列表按parentId分组，构建映射关系
        // key为父机构ID，value为该父机构下的所有子机构列表
        Map<Long, List<OrganizationVO>> parentMap = allList.stream()
            // 处理parentId为null的情况，将其转为0L（表示顶级机构）
            .collect(Collectors.groupingBy(org -> org.getParentId() == null ? 0L : org.getParentId()));
        
        // 递归构建树形结构，从顶级机构（parentId=0）开始
        List<Map<String, Object>> tree = buildTree(0L, parentMap);
        
        // 组装返回结果，包含树形结构和原始列表
        Map<String, Object> result = new HashMap<>();
        // 树形结构数据，用于前端树形组件展示
        result.put("tree", tree);
        // 原始列表数据，用于其他场景
        result.put("list", allList);
        
        // 返回成功结果，包含树形结构数据
        return Result.success("查询成功", result);
    }

    /**
     * 递归构建树形结构数据
     * 
     * 业务流程：
     * 1. 创建树节点列表
     * 2. 从父机构映射中获取当前父机构的所有子机构
     * 3. 遍历子机构，为每个子机构构建树节点
     * 4. 递归调用buildTree构建子机构的子节点
     * 5. 将节点添加到树列表中
     * 
     * @param parentId 父机构ID
     * @param parentMap 父机构-子机构映射关系
     * @return 树形结构节点列表
     */
    private List<Map<String, Object>> buildTree(Long parentId, Map<Long, List<OrganizationVO>> parentMap) {
        // 创建当前层级的树节点列表
        List<Map<String, Object>> tree = new ArrayList<>();
        // 从映射关系中获取当前父机构的所有子机构
        List<OrganizationVO> children = parentMap.get(parentId);
        
        // 如果存在子机构，遍历构建节点
        if (!CollectionUtils.isEmpty(children)) {
            // 遍历每个子机构，构建树节点
            for (OrganizationVO org : children) {
                // 创建树节点对象，包含节点信息和子节点
                Map<String, Object> node = new HashMap<>();
                // 设置节点ID，用于唯一标识
                node.put("id", org.getId());
                // 设置节点显示名称，用于树形组件展示
                node.put("label", org.getOrgName());
                // 设置节点值，与ID保持一致
                node.put("value", org.getId());
                // 设置机构类型，用于扩展功能
                node.put("orgType", org.getOrgType());
                // 设置机构简写，用于扩展功能
                node.put("orgShortName", org.getOrgShortName());
                // 递归调用构建子节点，实现无限层级
                node.put("children", buildTree(org.getId(), parentMap));
                // 将节点添加到当前层级的树列表中
                tree.add(node);
            }
        }
        
        // 返回当前层级的树形结构
        return tree;
    }

    /**
     * 校验机构数据DTO的必填字段
     * 
     * 校验规则：
     * 1. 机构名称不能为空字符串
     * 2. 使用StringUtils.hasText()方法校验，确保不是空字符串或仅包含空格
     * 
     * @param organizationDTO 机构数据DTO
     * @throws BusinessException 当机构名称为空时抛出
     */
    private void validateOrganizationDTO(OrganizationDTO organizationDTO) {
        // 使用Spring的StringUtils.hasText()方法校验机构名称
        // 该方法会校验字符串不为null、不为空字符串、且不只包含空白字符
        if (!StringUtils.hasText(organizationDTO.getOrgName())) {
            // 校验不通过，抛出业务异常
            throw new BusinessException("机构名称不能为空");
        }
    }

    /**
     * 校验父机构是否存在
     * 
     * 校验规则：
     * 1. 如果parentId为null或0，说明不选择父机构（即顶级机构），直接通过校验
     * 2. 如果parentId大于0，需要校验该父机构是否存在于数据库中
     * 3. 父机构不存在则抛出业务异常
     * 
     * @param parentId 父机构ID，null或0表示不选择父机构（顶级机构）
     * @throws BusinessException 当父机构不存在时抛出
     */
    private void validateParentExist(Long parentId) {
        // 校验父机构ID是否有效（不为null且大于0）
        // 如果parentId为null或0，表示不选择父机构（即顶级机构），不需要校验
        if (parentId != null && parentId > 0) {
            // 根据父机构ID查询父机构信息
            OrganizationVO parent = organizationMapper.selectById(parentId);
            // 父机构不存在则抛出业务异常
            if (parent == null) {
                throw new BusinessException("父机构不存在");
            }
        }
    }

    /**
     * 将机构DTO转换为实体对象
     * 
     * 转换规则：
     * 1. 创建新的Organization实体对象
     * 2. 使用Spring的BeanUtils.copyProperties()复制属性
     * 3. 属性名称相同的字段会自动复制
     * 
     * @param dto 机构数据DTO
     * @return 机构实体对象
     */
    private Organization convertToEntity(OrganizationDTO dto) {
        // 创建空的机构实体对象
        Organization organization = new Organization();
        // 使用Spring的BeanUtils工具类复制属性
        // 该方法会自动复制属性名称相同的字段值
        BeanUtils.copyProperties(dto, organization);
        // 返回转换后的实体对象
        return organization;
    }
}
