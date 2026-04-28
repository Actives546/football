package com.football.service.impl;

import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.dto.PersonDTO;
import com.football.dto.PersonDetailDTO;
import com.football.dto.PersonQueryDTO;
import com.football.entity.Person;
import com.football.entity.PersonDetail;
import com.football.mapper.OrganizationMapper;
import com.football.mapper.PersonDetailMapper;
import com.football.mapper.PersonMapper;
import com.football.service.PersonService;
import com.football.vo.OrganizationVO;
import com.football.vo.PersonDetailVO;
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
 * 支持人员(PERSON)和球员(PLAYER)两种类型的统一管理
 *
 * @author system
 * @version 1.0.0
 */
// 使用lombok的@Slf4j注解，自动生成日志记录器
@Slf4j
// 标记为Spring服务组件，由Spring容器管理
@Service
public class PersonServiceImpl implements PersonService {

    // 自动注入人员数据访问层Mapper
    @Autowired
    private PersonMapper personMapper;

    // 自动注入组织机构数据访问层Mapper，用于验证部门存在性
    @Autowired
    private OrganizationMapper organizationMapper;

    // 自动注入人员详细信息数据访问层Mapper
    @Autowired
    private PersonDetailMapper personDetailMapper;

    /**
     * 人员类型常量：普通人员
     * 用于标识系统中的普通工作人员
     */
    private static final String PERSON_TYPE_PERSON = "PERSON";

    /**
     * 人员类型常量：球员
     * 用于标识系统中的足球运动员
     */
    private static final String PERSON_TYPE_PLAYER = "PLAYER";

    /**
     * 根据ID查询人员详情
     * 实现PersonService接口中的getById方法
     *
     * @param id 人员ID，不能为空
     * @return Result<PersonVO> 包含人员详情的统一响应对象
     */
    @Override
    public Result<PersonVO> getById(Long id) {
        // 调用私有方法验证ID合法性并获取人员信息
        PersonVO personVO = validateAndGetPerson(id);
        // 返回查询成功的响应结果，包含人员详情
        return Result.success("查询成功", personVO);
    }

    /**
     * 分页查询人员列表
     * 实现PersonService接口中的getPage方法
     *
     * @param queryDTO 查询条件DTO，包含分页参数和过滤条件
     * @return Result<Map<String, Object>> 包含分页结果的统一响应对象
     */
    @Override
    public Result<Map<String, Object>> getPage(PersonQueryDTO queryDTO) {
        // 验证分页参数的合法性，不合法则设置默认值
        validatePaginationParams(queryDTO);

        // 调用Mapper执行分页查询，获取当前页的数据列表
        List<PersonVO> personList = personMapper.selectList(queryDTO);
        // 调用Mapper执行计数查询，获取满足条件的总记录数
        long total = personMapper.selectCount(queryDTO);

        // 构建分页结果Map并返回成功响应
        return Result.success("查询成功", buildPageResult(queryDTO, personList, total));
    }

    /**
     * 新增人员信息
     * 实现PersonService接口中的add方法
     * 使用@Transactional注解确保操作的事务性，任何异常都会回滚
     *
     * @param personDTO 人员信息DTO，包含新增人员的所有属性
     * @return Result<Boolean> 包含操作结果的统一响应对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(PersonDTO personDTO) {
        // 验证人员数据的完整性和合法性
        validatePersonData(personDTO);

        // 将DTO对象转换为实体对象，准备插入数据库
        Person person = convertToEntity(personDTO);

        // 调用Mapper执行插入操作，返回受影响的行数
        int rows = personMapper.insert(person);
        // 记录操作日志，记录新增成功的人员ID和姓名
        log.info("新增人员成功，人员ID：{}，人员名称：{}", person.getId(), person.getPersonName());

        // 返回新增成功的响应结果
        return Result.success("新增成功", rows > 0);
    }

    /**
     * 更新人员信息
     * 实现PersonService接口中的update方法
     * 使用@Transactional注解确保操作的事务性
     *
     * @param personDTO 人员信息DTO，包含更新后的人员属性
     * @return Result<Boolean> 包含操作结果的统一响应对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(PersonDTO personDTO) {
        // 首先验证人员是否存在，获取现有人员信息
        PersonVO existPerson = validateAndGetPerson(personDTO.getId());

        // 验证更新数据的完整性和合法性
        validatePersonData(personDTO);

        // 将DTO对象转换为实体对象，准备更新数据库
        Person person = convertToEntity(personDTO);

        // 调用Mapper执行更新操作，返回受影响的行数
        int rows = personMapper.updateById(person);
        // 记录操作日志，记录更新成功的人员ID和姓名
        log.info("更新人员成功，人员ID：{}，人员名称：{}", person.getId(), person.getPersonName());

        // 返回更新成功的响应结果
        return Result.success("更新成功", rows > 0);
    }

    /**
     * 根据ID删除人员（逻辑删除）
     * 实现PersonService接口中的delete方法
     * 使用@Transactional注解确保操作的事务性
     * 删除前会验证人员是否存在
     *
     * @param id 人员ID，不能为空
     * @return Result<Boolean> 包含操作结果的统一响应对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        // 验证人员是否存在，获取人员信息用于日志记录
        PersonVO person = validateAndGetPerson(id);

        // 调用Mapper执行逻辑删除操作（将deleted字段设为1）
        int rows = personMapper.deleteById(id);
        // 记录操作日志，记录删除成功的人员ID和姓名
        log.info("删除人员成功，人员ID：{}，人员名称：{}", id, person.getPersonName());

        // 返回删除成功的响应结果
        return Result.success("删除成功", rows > 0);
    }

    /**
     * 批量删除人员（逻辑删除）
     * 实现PersonService接口中的deleteBatch方法
     * 使用@Transactional注解确保操作的事务性
     * 删除前会校验所有ID对应的人员都存在
     *
     * @param ids 人员ID列表，不能为空
     * @return Result<Boolean> 包含操作结果的统一响应对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        // 检查ID列表是否为空，如果为空则抛出业务异常
        if (CollectionUtils.isEmpty(ids)) {
            // 抛出业务异常，提示用户选择要删除的人员
            throw new BusinessException("请选择要删除的人员");
        }

        // 遍历所有ID，校验每个人员都存在
        for (Long id : ids) {
            // 调用validateAndGetPerson方法验证人员存在性
            // 如果任何一个ID不存在，该方法会抛出BusinessException
            validateAndGetPerson(id);
        }

        // 调用Mapper执行批量逻辑删除操作
        int rows = personMapper.deleteByIds(ids);
        // 记录操作日志，记录批量删除成功的数量
        log.info("批量删除人员成功，删除数量：{}", rows);

        // 返回批量删除成功的响应结果
        return Result.success("批量删除成功", rows > 0);
    }

    /**
     * 根据部门ID查询该部门下的所有人员
     * 实现PersonService接口中的getByOrgId方法
     *
     * @param orgId 部门ID，不能为空
     * @return Result<List<PersonVO>> 包含人员列表的统一响应对象
     */
    @Override
    public Result<List<PersonVO>> getByOrgId(Long orgId) {
        // 检查部门ID是否为空，如果为空则抛出业务异常
        if (orgId == null) {
            // 抛出业务异常，提示部门ID不能为空
            throw new BusinessException("部门ID不能为空");
        }
        // 调用Mapper根据部门ID查询人员列表
        List<PersonVO> list = personMapper.selectByOrgId(orgId);
        // 返回查询成功的响应结果，包含人员列表
        return Result.success("查询成功", list);
    }

    /**
     * 验证人员ID并获取人员信息
     * 私有方法，用于内部验证和获取人员数据
     * 此方法在删除前会被调用，确保数据存在
     *
     * @param id 人员ID
     * @return PersonVO 人员信息VO对象
     * @throws BusinessException 当ID为空或人员不存在时抛出业务异常
     */
    private PersonVO validateAndGetPerson(Long id) {
        // 检查人员ID是否为空
        if (id == null) {
            // 抛出业务异常，提示人员ID不能为空
            throw new BusinessException("人员ID不能为空");
        }
        // 调用Mapper根据ID查询人员信息
        PersonVO person = personMapper.selectById(id);
        // 检查查询结果是否为空（人员不存在）
        if (person == null) {
            // 抛出业务异常，提示人员不存在
            throw new BusinessException("人员不存在");
        }
        // 返回验证通过的人员信息
        return person;
    }

    /**
     * 验证分页参数
     * 私有方法，用于确保分页参数的合法性
     * 如果参数不合法，则设置为默认值
     *
     * @param queryDTO 查询条件DTO，包含分页参数
     */
    private void validatePaginationParams(PersonQueryDTO queryDTO) {
        // 检查页码是否为空或小于等于0
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            // 设置默认页码为1
            queryDTO.setPageNum(1);
        }
        // 检查每页大小是否为空或小于等于0
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            // 设置默认每页大小为10
            queryDTO.setPageSize(10);
        }
    }

    /**
     * 验证人员数据
     * 私有方法，组合验证人员DTO和部门存在性
     *
     * @param personDTO 人员信息DTO
     */
    private void validatePersonData(PersonDTO personDTO) {
        // 第一步：验证人员DTO的必填字段
        validatePersonDTO(personDTO);
        // 第二步：验证所属部门是否存在
        validateOrgExist(personDTO.getOrgId());
    }

    /**
     * 构建分页结果Map
     * 私有方法，将分页查询结果组装成前端需要的Map格式
     *
     * @param queryDTO 查询条件DTO，包含当前页和每页大小
     * @param list     当前页的数据列表
     * @param total    满足条件的总记录数
     * @return Map<String, Object> 包含分页信息的Map对象
     */
    private Map<String, Object> buildPageResult(PersonQueryDTO queryDTO, List<PersonVO> list, long total) {
        // 创建HashMap用于存储分页结果
        Map<String, Object> result = new HashMap<>();
        // 存入数据列表
        result.put("list", list);
        // 存入总记录数
        result.put("total", total);
        // 存入当前页码
        result.put("pageNum", queryDTO.getPageNum());
        // 存入每页大小
        result.put("pageSize", queryDTO.getPageSize());
        // 返回组装好的分页结果Map
        return result;
    }

    /**
     * 验证人员DTO的必填字段
     * 私有方法，验证人员DTO中的必填字段是否为空以及人员类型是否合法
     *
     * @param personDTO 人员信息DTO
     * @throws BusinessException 当必填字段为空或人员类型不合法时抛出业务异常
     */
    private void validatePersonDTO(PersonDTO personDTO) {
        // 使用StringUtils.hasText检查人员姓名是否有实际内容（非空且非空白）
        if (!StringUtils.hasText(personDTO.getPersonName())) {
            // 抛出业务异常，提示人员姓名不能为空
            throw new BusinessException("人员姓名不能为空");
        }
        // 使用StringUtils.hasText检查人员类型是否有实际内容
        if (!StringUtils.hasText(personDTO.getPersonType())) {
            // 抛出业务异常，提示人员类型不能为空
            throw new BusinessException("人员类型不能为空");
        }
        // 检查人员类型是否为合法值（只能是PERSON或PLAYER）
        if (!PERSON_TYPE_PERSON.equals(personDTO.getPersonType()) 
                && !PERSON_TYPE_PLAYER.equals(personDTO.getPersonType())) {
            // 抛出业务异常，提示人员类型不合法
            throw new BusinessException("人员类型不合法");
        }
    }

    /**
     * 验证部门是否存在
     * 私有方法，根据部门ID查询数据库，确认部门存在
     *
     * @param orgId 部门ID
     * @throws BusinessException 当部门ID为空或部门不存在时抛出业务异常
     */
    private void validateOrgExist(Long orgId) {
        // 检查部门ID是否为空
        if (orgId == null) {
            // 抛出业务异常，提示所属部门不能为空
            throw new BusinessException("所属部门不能为空");
        }
        // 调用OrganizationMapper根据ID查询部门信息
        OrganizationVO org = organizationMapper.selectById(orgId);
        // 检查查询结果是否为空（部门不存在）
        if (org == null) {
            // 抛出业务异常，提示所属部门不存在
            throw new BusinessException("所属部门不存在");
        }
    }

    /**
     * 将PersonDTO转换为Person实体
     * 私有方法，使用Spring的BeanUtils进行属性拷贝
     *
     * @param dto 人员信息DTO
     * @return Person 实体对象
     */
    private Person convertToEntity(PersonDTO dto) {
        // 创建新的Person实体对象
        Person person = new Person();
        // 使用Spring BeanUtils将DTO的属性拷贝到实体对象
        BeanUtils.copyProperties(dto, person);
        // 返回转换后的实体对象
        return person;
    }

    /**
     * 根据人员ID查询完整详细信息（包含基本信息和补充信息）
     * 实现PersonService接口中的getDetailById方法
     *
     * @param personId 人员ID
     * @return Result<PersonDetailVO> 包含人员完整详细信息的统一响应对象
     */
    @Override
    public Result<PersonDetailVO> getDetailById(Long personId) {
        // 验证人员ID并获取基本信息（确保人员存在）
        validateAndGetPerson(personId);
        
        // 查询完整详细信息（包含基本信息和补充信息）
        PersonDetailVO detailVO = personDetailMapper.selectByPersonId(personId);
        
        // 返回查询成功的响应结果
        return Result.success("查询成功", detailVO);
    }

    /**
     * 保存人员详细信息（新增或更新）
     * 实现PersonService接口中的saveDetail方法
     * 使用@Transactional注解确保操作的事务性
     *
     * @param detailDTO 人员详细信息DTO
     * @return Result<Boolean> 包含操作结果的统一响应对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> saveDetail(PersonDetailDTO detailDTO) {
        // 验证人员ID是否为空
        if (detailDTO.getPersonId() == null) {
            throw new BusinessException("人员ID不能为空");
        }
        
        // 验证人员是否存在
        validateAndGetPerson(detailDTO.getPersonId());
        
        // 将DTO转换为实体对象
        PersonDetail personDetail = convertDetailToEntity(detailDTO);
        
        // 检查是否已存在详细信息记录
        PersonDetailVO existDetail = personDetailMapper.selectByPersonId(detailDTO.getPersonId());
        
        int rows;
        if (existDetail != null) {
            // 已存在，执行更新
            rows = personDetailMapper.updateByPersonId(personDetail);
            log.info("更新人员详细信息成功，人员ID：{}", detailDTO.getPersonId());
        } else {
            // 不存在，执行新增
            rows = personDetailMapper.insert(personDetail);
            log.info("新增人员详细信息成功，人员ID：{}", detailDTO.getPersonId());
        }
        
        // 返回操作结果
        return Result.success("保存成功", rows > 0);
    }

    /**
     * 将PersonDetailDTO转换为PersonDetail实体
     * 私有方法，使用Spring的BeanUtils进行属性拷贝
     *
     * @param dto 人员详细信息DTO
     * @return PersonDetail 实体对象
     */
    private PersonDetail convertDetailToEntity(PersonDetailDTO dto) {
        PersonDetail personDetail = new PersonDetail();
        BeanUtils.copyProperties(dto, personDetail);
        return personDetail;
    }
}
