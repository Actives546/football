package com.football.service;

import com.football.common.Result;
import com.football.dto.PersonDTO;
import com.football.dto.PersonDetailDTO;
import com.football.dto.PersonQueryDTO;
import com.football.vo.PersonDetailVO;
import com.football.vo.PersonVO;

import java.util.List;
import java.util.Map;

/**
 * 人员管理服务接口
 * 定义人员信息的业务逻辑接口
 *
 * @author system
 * @version 1.0.0
 */
public interface PersonService {

    /**
     * 根据ID查询人员详情
     *
     * @param id 人员ID
     * @return 人员详情信息
     */
    Result<PersonVO> getById(Long id);

    /**
     * 分页查询人员列表
     *
     * @param queryDTO 查询条件DTO
     * @return 分页结果，包含list（数据列表）、total（总记录数）、pageNum（当前页）、pageSize（每页大小）
     */
    Result<Map<String, Object>> getPage(PersonQueryDTO queryDTO);

    /**
     * 新增人员信息
     *
     * @param personDTO 人员信息DTO
     * @return 操作结果，true表示成功
     */
    Result<Boolean> add(PersonDTO personDTO);

    /**
     * 更新人员信息
     *
     * @param personDTO 人员信息DTO
     * @return 操作结果，true表示成功
     */
    Result<Boolean> update(PersonDTO personDTO);

    /**
     * 根据ID删除人员（逻辑删除）
     *
     * @param id 人员ID
     * @return 操作结果，true表示成功
     */
    Result<Boolean> delete(Long id);

    /**
     * 批量删除人员（逻辑删除）
     *
     * @param ids 人员ID列表
     * @return 操作结果，true表示成功
     */
    Result<Boolean> deleteBatch(List<Long> ids);

    /**
     * 根据部门ID查询该部门下的所有人员
     *
     * @param orgId 部门ID
     * @return 人员列表
     */
    Result<List<PersonVO>> getByOrgId(Long orgId);

    /**
     * 根据人员ID查询完整详细信息（包含基本信息和补充信息）
     *
     * @param personId 人员ID
     * @return 人员完整详细信息
     */
    Result<PersonDetailVO> getDetailById(Long personId);

    /**
     * 保存人员详细信息（新增或更新）
     *
     * @param detailDTO 人员详细信息DTO
     * @return 操作结果
     */
    Result<Boolean> saveDetail(PersonDetailDTO detailDTO);
}
