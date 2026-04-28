package com.football.service.impl;

import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.dto.PersonDTO;
import com.football.dto.PersonQueryDTO;
import com.football.entity.Person;
import com.football.mapper.OrganizationMapper;
import com.football.mapper.PersonMapper;
import com.football.service.PersonService;
import com.football.vo.OrganizationVO;
import com.football.vo.PersonVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员管理服务实现类
 * 实现人员信息的增删改查业务逻辑
 *
 * @author system
 * @version 1.0.0
 */
@Slf4j
@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private OrganizationMapper organizationMapper;

    /**
     * 人员类型常量：普通人员
     */
    private static final String PERSON_TYPE_PERSON = "PERSON";

    /**
     * 人员类型常量：球员
     */
    private static final String PERSON_TYPE_PLAYER = "PLAYER";

    @Override
    public Result<PersonVO> getById(Long id) {
        PersonVO personVO = validateAndGetPerson(id);
        return Result.success("查询成功", personVO);
    }

    @Override
    public Result<Map<String, Object>> getPage(PersonQueryDTO queryDTO) {
        validatePaginationParams(queryDTO);

        List<PersonVO> personList = personMapper.selectList(queryDTO);
        long total = personMapper.selectCount(queryDTO);

        return Result.success("查询成功", buildPageResult(queryDTO, personList, total));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(PersonDTO personDTO) {
        validatePersonData(personDTO);

        Person person = convertToEntity(personDTO);

        int rows = personMapper.insert(person);
        log.info("新增人员成功，人员ID：{}，人员名称：{}", person.getId(), person.getPersonName());

        return Result.success("新增成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(PersonDTO personDTO) {
        PersonVO existPerson = validateAndGetPerson(personDTO.getId());

        validatePersonData(personDTO);

        Person person = convertToEntity(personDTO);

        int rows = personMapper.updateById(person);
        log.info("更新人员成功，人员ID：{}，人员名称：{}", person.getId(), person.getPersonName());

        return Result.success("更新成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        PersonVO person = validateAndGetPerson(id);

        int rows = personMapper.deleteById(id);
        log.info("删除人员成功，人员ID：{}，人员名称：{}", id, person.getPersonName());

        return Result.success("删除成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的人员");
        }

        int rows = personMapper.deleteByIds(ids);
        log.info("批量删除人员成功，删除数量：{}", rows);

        return Result.success("批量删除成功", rows > 0);
    }

    @Override
    public Result<List<PersonVO>> getByOrgId(Long orgId) {
        if (orgId == null) {
            throw new BusinessException("部门ID不能为空");
        }
        List<PersonVO> list = personMapper.selectByOrgId(orgId);
        return Result.success("查询成功", list);
    }

    /**
     * 验证人员ID并获取人员信息
     *
     * @param id 人员ID
     * @return 人员信息VO
     * @throws BusinessException 当ID为空或人员不存在时抛出
     */
    private PersonVO validateAndGetPerson(Long id) {
        if (id == null) {
            throw new BusinessException("人员ID不能为空");
        }
        PersonVO person = personMapper.selectById(id);
        if (person == null) {
            throw new BusinessException("人员不存在");
        }
        return person;
    }

    /**
     * 验证分页参数
     * 如果分页参数不合法，则设置默认值
     *
     * @param queryDTO 查询条件DTO
     */
    private void validatePaginationParams(PersonQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(10);
        }
    }

    /**
     * 验证人员数据
     * 包括人员DTO验证和部门存在性验证
     *
     * @param personDTO 人员信息DTO
     */
    private void validatePersonData(PersonDTO personDTO) {
        validatePersonDTO(personDTO);
        validateOrgExist(personDTO.getOrgId());
    }

    /**
     * 构建分页结果Map
     *
     * @param queryDTO 查询条件DTO
     * @param list     数据列表
     * @param total    总记录数
     * @return 包含分页信息的Map
     */
    private Map<String, Object> buildPageResult(PersonQueryDTO queryDTO, List<PersonVO> list, long total) {
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", queryDTO.getPageNum());
        result.put("pageSize", queryDTO.getPageSize());
        return result;
    }

    /**
     * 验证人员DTO的必填字段
     *
     * @param personDTO 人员信息DTO
     * @throws BusinessException 当必填字段为空或人员类型不合法时抛出
     */
    private void validatePersonDTO(PersonDTO personDTO) {
        if (!StringUtils.hasText(personDTO.getPersonName())) {
            throw new BusinessException("人员姓名不能为空");
        }
        if (!StringUtils.hasText(personDTO.getPersonType())) {
            throw new BusinessException("人员类型不能为空");
        }
        if (!PERSON_TYPE_PERSON.equals(personDTO.getPersonType()) 
                && !PERSON_TYPE_PLAYER.equals(personDTO.getPersonType())) {
            throw new BusinessException("人员类型不合法");
        }
    }

    /**
     * 验证部门是否存在
     *
     * @param orgId 部门ID
     * @throws BusinessException 当部门ID为空或部门不存在时抛出
     */
    private void validateOrgExist(Long orgId) {
        if (orgId == null) {
            throw new BusinessException("所属部门不能为空");
        }
        OrganizationVO org = organizationMapper.selectById(orgId);
        if (org == null) {
            throw new BusinessException("所属部门不存在");
        }
    }

    /**
     * 将PersonDTO转换为Person实体
     *
     * @param dto 人员信息DTO
     * @return Person实体对象
     */
    private Person convertToEntity(PersonDTO dto) {
        Person person = new Person();
        BeanUtils.copyProperties(dto, person);
        return person;
    }
}
