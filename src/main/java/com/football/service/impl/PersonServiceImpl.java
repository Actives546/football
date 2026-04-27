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

@Slf4j
@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private OrganizationMapper organizationMapper;

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

    private void validatePaginationParams(PersonQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(10);
        }
    }

    private void validatePersonData(PersonDTO personDTO) {
        validatePersonDTO(personDTO);
        validateOrgExist(personDTO.getOrgId());
    }

    private Map<String, Object> buildPageResult(PersonQueryDTO queryDTO, List<PersonVO> list, long total) {
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", queryDTO.getPageNum());
        result.put("pageSize", queryDTO.getPageSize());
        return result;
    }

    private void validatePersonDTO(PersonDTO personDTO) {
        if (!StringUtils.hasText(personDTO.getPersonName())) {
            throw new BusinessException("人员姓名不能为空");
        }
        if (!StringUtils.hasText(personDTO.getPersonType())) {
            throw new BusinessException("人员类型不能为空");
        }
        if (!"PERSON".equals(personDTO.getPersonType()) && !"PLAYER".equals(personDTO.getPersonType())) {
            throw new BusinessException("人员类型不合法");
        }
    }

    private void validateOrgExist(Long orgId) {
        if (orgId == null) {
            throw new BusinessException("所属部门不能为空");
        }
        OrganizationVO org = organizationMapper.selectById(orgId);
        if (org == null) {
            throw new BusinessException("所属部门不存在");
        }
    }

    private Person convertToEntity(PersonDTO dto) {
        Person person = new Person();
        BeanUtils.copyProperties(dto, person);
        return person;
    }
}
