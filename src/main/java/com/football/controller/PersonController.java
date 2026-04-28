package com.football.controller;

import com.football.common.Result;
import com.football.dto.PersonDTO;
import com.football.dto.PersonQueryDTO;
import com.football.service.PersonService;
import com.football.vo.PersonVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 人员管理控制器
 * 提供人员信息的增删改查等RESTful API接口
 * 支持人员(PERSON)和球员(PLAYER)两种类型
 *
 * @author system
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * 根据ID查询人员详情
     *
     * @param id 人员ID
     * @return 人员详情信息
     */
    @GetMapping("/{id}")
    public Result<PersonVO> getById(@PathVariable Long id) {
        return personService.getById(id);
    }

    /**
     * 分页查询人员列表
     *
     * @param queryDTO 查询条件DTO
     * @return 分页结果，包含列表数据和总记录数
     */
    @PostMapping("/page")
    public Result<Map<String, Object>> getPage(@RequestBody PersonQueryDTO queryDTO) {
        return personService.getPage(queryDTO);
    }

    /**
     * 根据部门ID查询该部门下的所有人员
     *
     * @param orgId 部门ID
     * @return 人员列表
     */
    @GetMapping("/org/{orgId}")
    public Result<List<PersonVO>> getByOrgId(@PathVariable Long orgId) {
        return personService.getByOrgId(orgId);
    }

    /**
     * 新增人员信息
     *
     * @param personDTO 人员信息DTO
     * @return 操作结果，true表示成功
     */
    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody PersonDTO personDTO) {
        return personService.add(personDTO);
    }

    /**
     * 更新人员信息
     *
     * @param personDTO 人员信息DTO
     * @return 操作结果，true表示成功
     */
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody PersonDTO personDTO) {
        return personService.update(personDTO);
    }

    /**
     * 根据ID删除人员（逻辑删除）
     *
     * @param id 人员ID
     * @return 操作结果，true表示成功
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return personService.delete(id);
    }

    /**
     * 批量删除人员（逻辑删除）
     *
     * @param ids 人员ID列表
     * @return 操作结果，true表示成功
     */
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return personService.deleteBatch(ids);
    }
}
