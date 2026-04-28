package com.football.controller;

import com.football.common.Result;
import com.football.dto.TeamDTO;
import com.football.dto.TeamQueryDTO;
import com.football.service.TeamService;
import com.football.vo.TeamVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 球队管理控制器
 * 
 * 提供球队管理相关的RESTful API接口
 * 1. 球队CRUD操作
 * 2. 分页查询
 * 3. 批量操作
 * 4. 数据导出
 * 
 * @author system
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    /**
     * 根据ID查询球队详情
     * 
     * @param id 球队ID
     * @return 球队详情
     */
    @GetMapping("/{id}")
    public Result<TeamVO> getById(@PathVariable Long id) {
        return teamService.getById(id);
    }

    /**
     * 查询所有球队列表（不分页）
     * 
     * @return 所有球队列表
     */
    @GetMapping("/all")
    public Result<List<TeamVO>> getAll() {
        return teamService.getAll();
    }

    /**
     * 分页查询球队列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    public Result<Map<String, Object>> getPage(@RequestBody TeamQueryDTO queryDTO) {
        return teamService.getPage(queryDTO);
    }

    /**
     * 新增球队
     * 
     * @param teamDTO 球队数据
     * @return 新增是否成功
     */
    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody TeamDTO teamDTO) {
        return teamService.add(teamDTO);
    }

    /**
     * 更新球队信息
     * 
     * @param teamDTO 球队数据
     * @return 更新是否成功
     */
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody TeamDTO teamDTO) {
        return teamService.update(teamDTO);
    }

    /**
     * 根据ID删除球队（逻辑删除）
     * 
     * @param id 球队ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return teamService.delete(id);
    }

    /**
     * 批量删除球队（逻辑删除）
     * 
     * @param ids 球队ID列表
     * @return 删除是否成功
     */
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return teamService.deleteBatch(ids);
    }

    /**
     * 导出球队数据
     * 
     * @param queryDTO 查询条件
     * @return 导出的数据列表
     */
    @PostMapping("/export")
    public Result<List<TeamVO>> export(@RequestBody TeamQueryDTO queryDTO) {
        return teamService.export(queryDTO);
    }

    /**
     * 批量启用球队
     * 
     * @param ids 球队ID列表
     * @return 更新是否成功
     */
    @PostMapping("/enable")
    public Result<Boolean> enableBatch(@RequestBody List<Long> ids) {
        return teamService.updateStatusBatch(ids, 1);
    }

    /**
     * 批量禁用球队
     * 
     * @param ids 球队ID列表
     * @return 更新是否成功
     */
    @PostMapping("/disable")
    public Result<Boolean> disableBatch(@RequestBody List<Long> ids) {
        return teamService.updateStatusBatch(ids, 0);
    }

    /**
     * 导入球队数据
     * 
     * 支持的文件格式：.xls、.xlsx、.csv
     * Excel列顺序：球队名称、地区、成立时间、主场、主教练、联系人、状态、简介
     * 状态值：启用/1 或 禁用/0
     * 
     * @param file 上传的文件
     * @return 导入结果，包含成功数量、失败数量和失败详情
     */
    @PostMapping("/import")
    public Result<Map<String, Object>> importTeams(@RequestParam("file") MultipartFile file) {
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件字节数组
            byte[] fileBytes = file.getBytes();
            // 调用Service层进行导入
            return teamService.importTeams(fileBytes, fileName);
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败：" + e.getMessage(), e);
        }
    }
}
