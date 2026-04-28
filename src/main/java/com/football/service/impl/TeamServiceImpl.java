package com.football.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.dto.TeamDTO;
import com.football.dto.TeamQueryDTO;
import com.football.entity.Team;
import com.football.mapper.TeamMapper;
import com.football.service.TeamService;
import com.football.vo.TeamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 球队管理业务实现类
 * 
 * 1. 实现球队的增删改查等核心业务逻辑
 * 2. 处理球队名称唯一性校验
 * 3. 提供分页查询、批量操作、数据导出等扩展功能
 * 4. 实现逻辑删除，确保数据可追溯
 * 
 * @author system
 * @version 1.0.0
 */
@Slf4j
@Service
public class TeamServiceImpl implements TeamService {

    /** 球队数据访问层 */
    @Autowired
    private TeamMapper teamMapper;

    /**
     * 根据ID查询球队详情
     * 
     * 业务流程：
     * 1. 校验球队ID不能为空
     * 2. 调用Mapper查询球队信息
     * 3. 校验查询结果，球队不存在则抛出业务异常
     * 4. 设置状态文本描述
     * 5. 返回成功结果，包含球队详情
     * 
     * @param id 球队ID
     * @return 球队详情视图对象
     * @throws BusinessException 当球队ID为空或球队不存在时抛出
     */
    @Override
    public Result<TeamVO> getById(Long id) {
        // 校验球队ID不能为空，为空则抛出业务异常
        if (id == null) {
            throw new BusinessException("球队ID不能为空");
        }
        // 根据ID查询球队详情
        TeamVO teamVO = teamMapper.selectById(id);
        // 校验球队是否存在，不存在则抛出业务异常
        if (teamVO == null) {
            throw new BusinessException("球队不存在");
        }
        // 设置状态文本描述
        teamVO.setStatusText(getStatusText(teamVO.getStatus()));
        // 返回成功结果，包含球队详情信息
        return Result.success("查询成功", teamVO);
    }

    /**
     * 分页查询球队列表
     * 
     * 业务流程：
     * 1. 校验分页参数，设置默认值（pageNum默认1，pageSize默认10）
     * 2. 调用Mapper查询球队列表（支持按球队名称模糊查询、地区模糊查询、状态精确查询）
     * 3. 调用Mapper查询符合条件的总记录数
     * 4. 为每条记录设置状态文本描述
     * 5. 组装分页结果返回，包含列表、总数、页码、每页数量
     * 
     * @param queryDTO 查询条件DTO
     * @return 分页结果，包含列表、总数、页码、每页数量
     */
    @Override
    public Result<Map<String, Object>> getPage(TeamQueryDTO queryDTO) {
        // 校验页码参数，为空或小于等于0时设置默认值为1
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }
        // 校验每页数量参数，为空或小于等于0时设置默认值为10
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(10);
        }

        // 根据查询条件分页查询球队列表
        List<TeamVO> teamList = teamMapper.selectList(queryDTO);
        // 为每条记录设置状态文本描述
        teamList.forEach(team -> team.setStatusText(getStatusText(team.getStatus())));
        // 查询符合条件的总记录数，用于分页
        long total = teamMapper.selectCount(queryDTO);

        // 组装返回结果，包含列表数据、总记录数、当前页码、每页数量
        Map<String, Object> result = new HashMap<>();
        // 设置球队列表数据
        result.put("list", teamList);
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
     * 新增球队
     * 
     * 业务流程：
     * 1. 校验球队数据DTO的必填字段（球队名称不能为空）
     * 2. 校验球队名称是否已存在（唯一性校验）
     * 3. 将DTO转换为实体对象
     * 4. 执行插入操作，自动生成ID
     * 5. 记录操作日志
     * 6. 返回新增是否成功
     * 
     * @param teamDTO 球队数据DTO
     * @return 新增是否成功
     * @throws BusinessException 当数据校验失败或球队名称已存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(TeamDTO teamDTO) {
        // 校验球队DTO的必填字段（球队名称不能为空）
        validateTeamDTO(teamDTO);
        // 校验球队名称是否已存在（唯一性校验）
        validateTeamNameUnique(teamDTO.getTeamName(), null);

        // 将DTO转换为实体对象，准备插入数据库
        Team team = convertToEntity(teamDTO);

        // 执行插入操作，返回影响的行数
        int rows = teamMapper.insert(team);
        // 记录操作日志，便于问题排查和审计
        log.info("新增球队成功，球队ID：{}，球队名称：{}", team.getId(), team.getTeamName());

        // 返回新增是否成功（影响行数大于0表示成功）
        return Result.success("新增成功", rows > 0);
    }

    /**
     * 更新球队信息
     * 
     * 业务流程：
     * 1. 校验球队ID不能为空
     * 2. 校验球队是否存在（根据ID查询）
     * 3. 校验更新数据的必填字段
     * 4. 校验球队名称是否已存在（排除自身）
     * 5. 执行更新操作
     * 6. 记录操作日志
     * 7. 返回更新是否成功
     * 
     * @param teamDTO 球队数据DTO
     * @return 更新是否成功
     * @throws BusinessException 当球队不存在或数据校验失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(TeamDTO teamDTO) {
        // 校验球队ID不能为空，为空则抛出业务异常
        if (teamDTO.getId() == null) {
            throw new BusinessException("球队ID不能为空");
        }

        // 根据ID查询球队信息，校验球队是否存在
        TeamVO existTeam = teamMapper.selectById(teamDTO.getId());
        // 球队不存在则抛出业务异常
        if (existTeam == null) {
            throw new BusinessException("球队不存在");
        }

        // 校验球队DTO的必填字段（球队名称不能为空）
        validateTeamDTO(teamDTO);
        // 校验球队名称是否已存在（排除自身）
        validateTeamNameUnique(teamDTO.getTeamName(), teamDTO.getId());

        // 将DTO转换为实体对象，准备更新数据库
        Team team = convertToEntity(teamDTO);

        // 执行更新操作，返回影响的行数
        int rows = teamMapper.updateById(team);
        // 记录操作日志，便于问题排查和审计
        log.info("更新球队成功，球队ID：{}，球队名称：{}", team.getId(), team.getTeamName());

        // 返回更新是否成功（影响行数大于0表示成功）
        return Result.success("更新成功", rows > 0);
    }

    /**
     * 根据ID删除球队（逻辑删除）
     * 
     * 业务流程：
     * 1. 校验球队ID不能为空
     * 2. 校验球队是否存在
     * 3. 执行逻辑删除操作（更新deleted字段为1）
     * 4. 记录删除日志
     * 5. 返回删除是否成功
     * 
     * @param id 球队ID
     * @return 删除是否成功
     * @throws BusinessException 当球队ID为空或球队不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        // 校验球队ID不能为空，为空则抛出业务异常
        if (id == null) {
            throw new BusinessException("球队ID不能为空");
        }

        // 根据ID查询球队信息，校验球队是否存在
        TeamVO team = teamMapper.selectById(id);
        // 球队不存在则抛出业务异常
        if (team == null) {
            throw new BusinessException("球队不存在");
        }

        // 执行逻辑删除操作，更新deleted字段为1，同时更新update_time
        int rows = teamMapper.deleteById(id);
        // 记录操作日志，便于问题排查和审计
        log.info("删除球队成功，球队ID：{}，球队名称：{}", id, team.getTeamName());

        // 返回删除是否成功（影响行数大于0表示成功）
        return Result.success("删除成功", rows > 0);
    }

    /**
     * 批量删除球队（逻辑删除）
     * 
     * 业务流程：
     * 1. 校验待删除的ID列表不能为空
     * 2. 执行批量逻辑删除操作
     * 3. 记录删除数量日志
     * 4. 返回删除是否成功
     * 
     * @param ids 球队ID列表
     * @return 删除是否成功
     * @throws BusinessException 当ID列表为空时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        // 校验待删除的ID列表不能为空，为空则抛出业务异常
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的球队");
        }

        // 执行批量逻辑删除操作，一次性更新所有符合条件的记录
        int rows = teamMapper.deleteByIds(ids);
        // 记录操作日志，便于问题排查和审计
        log.info("批量删除球队成功，删除数量：{}", rows);

        // 返回删除是否成功（影响行数大于0表示成功）
        return Result.success("批量删除成功", rows > 0);
    }

    /**
     * 查询所有球队列表（不分页）
     * 
     * 业务流程：
     * 1. 调用Mapper查询所有未删除的球队
     * 2. 为每条记录设置状态文本描述
     * 3. 返回完整的球队列表（按创建时间降序排列）
     * 
     * @return 所有球队列表
     */
    @Override
    public Result<List<TeamVO>> getAll() {
        // 查询所有未删除的球队列表，不进行分页
        List<TeamVO> list = teamMapper.selectAll();
        // 为每条记录设置状态文本描述
        list.forEach(team -> team.setStatusText(getStatusText(team.getStatus())));
        // 返回成功结果，包含所有球队列表
        return Result.success("查询成功", list);
    }

    /**
     * 导出球队数据
     * 
     * 业务流程：
     * 1. 调用Mapper查询所有符合条件的球队（不分页）
     * 2. 为每条记录设置状态文本描述
     * 3. 返回导出的数据列表
     * 
     * @param queryDTO 查询条件DTO
     * @return 导出的球队列表
     */
    @Override
    public Result<List<TeamVO>> export(TeamQueryDTO queryDTO) {
        // 临时保存分页参数
        Integer pageNum = queryDTO.getPageNum();
        Integer pageSize = queryDTO.getPageSize();
        
        // 导出时不分页，查询所有符合条件的数据
        queryDTO.setPageNum(null);
        queryDTO.setPageSize(null);
        
        // 查询所有符合条件的球队列表
        List<TeamVO> list = teamMapper.selectList(queryDTO);
        // 为每条记录设置状态文本描述
        list.forEach(team -> team.setStatusText(getStatusText(team.getStatus())));
        
        // 恢复分页参数
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        
        // 返回成功结果，包含导出的球队列表
        return Result.success("查询成功", list);
    }

    /**
     * 批量更新球队状态
     * 
     * 业务流程：
     * 1. 校验待更新的ID列表不能为空
     * 2. 校验状态值是否有效（0或1）
     * 3. 遍历每个ID，逐个更新状态
     * 4. 记录操作日志
     * 5. 返回更新是否成功
     * 
     * @param ids 球队ID列表
     * @param status 状态值（0-禁用，1-启用）
     * @return 更新是否成功
     * @throws BusinessException 当ID列表为空或状态值无效时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateStatusBatch(List<Long> ids, Integer status) {
        // 校验待更新的ID列表不能为空，为空则抛出业务异常
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要更新的球队");
        }
        
        // 校验状态值是否有效（必须是0或1）
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值无效");
        }
        
        // 遍历每个ID，逐个更新状态
        int successCount = 0;
        for (Long id : ids) {
            Team team = new Team();
            team.setId(id);
            team.setStatus(status);
            int rows = teamMapper.updateById(team);
            if (rows > 0) {
                successCount++;
            }
        }
        
        // 记录操作日志，便于问题排查和审计
        log.info("批量更新球队状态成功，成功数量：{}，目标状态：{}", successCount, getStatusText(status));
        
        // 返回更新是否成功（成功数量大于0表示成功）
        return Result.success("批量更新状态成功，成功数量：" + successCount, successCount > 0);
    }

    /**
     * 获取状态文本描述
     * 
     * @param status 状态值（0-禁用，1-启用）
     * @return 状态文本描述
     */
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "启用" : "禁用";
    }

    /**
     * 校验球队数据DTO的必填字段
     * 
     * 校验规则：
     * 1. 球队名称不能为空字符串
     * 2. 使用StringUtils.hasText()方法校验，确保不是空字符串或仅包含空格
     * 
     * @param teamDTO 球队数据DTO
     * @throws BusinessException 当球队名称为空时抛出
     */
    private void validateTeamDTO(TeamDTO teamDTO) {
        // 使用Spring的StringUtils.hasText()方法校验球队名称
        // 该方法会校验字符串不为null、不为空字符串、且不只包含空白字符
        if (!StringUtils.hasText(teamDTO.getTeamName())) {
            // 校验不通过，抛出业务异常
            throw new BusinessException("球队名称不能为空");
        }
    }

    /**
     * 校验球队名称是否唯一
     * 
     * 校验规则：
     * 1. 查询是否存在相同名称的球队
     * 2. 如果是编辑操作（excludeId不为null），则排除自身
     * 3. 如果存在相同名称的球队，则抛出业务异常
     * 
     * @param teamName 球队名称
     * @param excludeId 排除的ID（编辑时使用，新增时为null）
     * @throws BusinessException 当球队名称已存在时抛出
     */
    private void validateTeamNameUnique(String teamName, Long excludeId) {
        // 查询是否存在相同名称的球队
        TeamVO existTeam = teamMapper.selectByName(teamName, excludeId);
        // 如果存在相同名称的球队，则抛出业务异常
        if (existTeam != null) {
            throw new BusinessException("球队名称已存在：" + teamName);
        }
    }

    /**
     * 将球队DTO转换为实体对象
     * 
     * 转换规则：
     * 1. 创建新的Team实体对象
     * 2. 使用Spring的BeanUtils.copyProperties()复制属性
     * 3. 属性名称相同的字段会自动复制
     * 
     * @param dto 球队数据DTO
     * @return 球队实体对象
     */
    private Team convertToEntity(TeamDTO dto) {
        // 创建空的球队实体对象
        Team team = new Team();
        // 使用Spring的BeanUtils工具类复制属性
        // 该方法会自动复制属性名称相同的字段值
        BeanUtils.copyProperties(dto, team);
        // 返回转换后的实体对象
        return team;
    }

    /**
     * 导入球队数据
     * 
     * 业务流程：
     * 1. 校验文件格式（支持.xls、.xlsx、.csv）
     * 2. 使用Hutool的ExcelReader解析文件内容
     * 3. 遍历每一行数据，进行数据校验
     * 4. 校验球队名称唯一性
     * 5. 批量插入数据库
     * 6. 返回导入结果（成功数量、失败数量、失败详情）
     * 
     * @param file 文件字节数组
     * @param fileName 文件名
     * @return 导入结果，包含成功数量、失败数量和失败详情
     * @throws BusinessException 当文件格式不支持或解析失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> importTeams(byte[] file, String fileName) {
        // 校验文件是否为空
        if (file == null || file.length == 0) {
            throw new BusinessException("上传文件不能为空");
        }

        // 校验文件格式
        String fileExtension = getFileExtension(fileName);
        if (!isSupportedFormat(fileExtension)) {
            throw new BusinessException("不支持的文件格式，仅支持 .xls、.xlsx、.csv 格式");
        }

        // 记录导入操作日志
        log.info("开始导入球队数据，文件名：{}", fileName);

        // 用于跟踪导入结果
        int successCount = 0;
        int failCount = 0;
        List<Map<String, Object>> failDetails = new ArrayList<>();

        // 用于存储已验证的球队名称，避免同一批导入中重复
        Map<String, Integer> nameRowMap = new HashMap<>();

        // 日期格式化器，支持多种格式
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyy年MM月dd日")
        };

        try (InputStream inputStream = new ByteArrayInputStream(file)) {
            // 使用Hutool的ExcelUtil读取文件
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            
            // 获取所有行数据（忽略表头）
            List<List<Object>> rows = reader.read();
            
            // 检查是否有数据
            if (rows == null || rows.size() <= 1) {
                throw new BusinessException("文件中没有有效数据");
            }

            // 从第二行开始遍历数据（跳过表头）
            for (int i = 1; i < rows.size(); i++) {
                List<Object> row = rows.get(i);
                int rowNum = i + 1; // Excel行号从1开始

                // 检查行是否为空
                if (isRowEmpty(row)) {
                    continue;
                }

                try {
                    // 解析每一列数据
                    // 列索引：0-球队名称, 1-地区, 2-成立时间, 3-主场, 4-主教练, 5-联系人, 6-状态, 7-简介
                    String teamName = getCellValueAsString(row, 0);
                    String region = getCellValueAsString(row, 1);
                    String foundingDateStr = getCellValueAsString(row, 2);
                    String homeStadium = getCellValueAsString(row, 3);
                    String headCoach = getCellValueAsString(row, 4);
                    String contactPerson = getCellValueAsString(row, 5);
                    String statusStr = getCellValueAsString(row, 6);
                    String description = getCellValueAsString(row, 7);

                    // 校验必填字段：球队名称
                    if (!StringUtils.hasText(teamName)) {
                        throw new BusinessException("球队名称不能为空");
                    }

                    // 校验同一批导入中是否有重复的球队名称
                    if (nameRowMap.containsKey(teamName)) {
                        throw new BusinessException("球队名称与第" + nameRowMap.get(teamName) + "行重复");
                    }
                    nameRowMap.put(teamName, rowNum);

                    // 校验数据库中是否已存在同名球队
                    TeamVO existTeam = teamMapper.selectByName(teamName, null);
                    if (existTeam != null) {
                        throw new BusinessException("球队名称已存在");
                    }

                    // 解析状态
                    Integer status = parseStatus(statusStr);

                    // 解析成立时间
                    LocalDate foundingDate = parseLocalDate(foundingDateStr, formatters);

                    // 构建球队实体
                    Team team = new Team();
                    team.setTeamName(teamName.trim());
                    team.setRegion(StringUtils.hasText(region) ? region.trim() : null);
                    team.setFoundingDate(foundingDate);
                    team.setHomeStadium(StringUtils.hasText(homeStadium) ? homeStadium.trim() : null);
                    team.setHeadCoach(StringUtils.hasText(headCoach) ? headCoach.trim() : null);
                    team.setContactPerson(StringUtils.hasText(contactPerson) ? contactPerson.trim() : null);
                    team.setStatus(status);
                    team.setDescription(StringUtils.hasText(description) ? description.trim() : null);

                    // 插入数据库
                    int rowsAffected = teamMapper.insert(team);
                    if (rowsAffected > 0) {
                        successCount++;
                        log.info("导入球队成功：{}", teamName);
                    } else {
                        throw new BusinessException("数据库插入失败");
                    }

                } catch (BusinessException e) {
                    // 业务异常，记录失败详情
                    failCount++;
                    Map<String, Object> failDetail = new HashMap<>();
                    failDetail.put("rowNum", rowNum);
                    failDetail.put("error", e.getMessage());
                    failDetails.add(failDetail);
                    log.warn("导入球队失败，行号：{}，原因：{}", rowNum, e.getMessage());
                } catch (Exception e) {
                    // 其他异常，记录失败详情
                    failCount++;
                    Map<String, Object> failDetail = new HashMap<>();
                    failDetail.put("rowNum", rowNum);
                    failDetail.put("error", "数据格式错误：" + e.getMessage());
                    failDetails.add(failDetail);
                    log.error("导入球队发生异常，行号：{}", rowNum, e);
                }
            }

        } catch (IOException e) {
            log.error("读取导入文件失败", e);
            throw new BusinessException("读取文件失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("解析导入文件失败", e);
            throw new BusinessException("解析文件失败：" + e.getMessage());
        }

        // 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failDetails", failDetails);

        // 记录导入结果日志
        log.info("导入球队数据完成，成功：{}，失败：{}", successCount, failCount);

        // 返回导入结果
        String message = String.format("导入完成，成功：%d 条，失败：%d 条", successCount, failCount);
        return Result.success(message, result);
    }

    /**
     * 获取文件扩展名
     * 
     * @param fileName 文件名
     * @return 小写的文件扩展名（不带点）
     */
    private String getFileExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 检查是否是支持的文件格式
     * 
     * @param extension 文件扩展名
     * @return 是否支持
     */
    private boolean isSupportedFormat(String extension) {
        return "xls".equals(extension) || "xlsx".equals(extension) || "csv".equals(extension);
    }

    /**
     * 检查行是否为空
     * 
     * @param row 行数据
     * @return 是否为空
     */
    private boolean isRowEmpty(List<Object> row) {
        if (row == null || row.isEmpty()) {
            return true;
        }
        for (Object cell : row) {
            if (cell != null && StringUtils.hasText(cell.toString().trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取单元格的值作为字符串
     * 
     * @param row 行数据
     * @param index 列索引
     * @return 字符串值（去空格后）
     */
    private String getCellValueAsString(List<Object> row, int index) {
        if (row == null || index >= row.size()) {
            return "";
        }
        Object value = row.get(index);
        if (value == null) {
            return "";
        }
        return value.toString().trim();
    }

    /**
     * 解析状态值
     * 
     * 支持的格式：
     * - "启用"、"1"、1 → 1
     * - "禁用"、"0"、0 → 0
     * - 其他默认 → 1
     * 
     * @param statusStr 状态字符串
     * @return 状态值
     */
    private Integer parseStatus(String statusStr) {
        if (!StringUtils.hasText(statusStr)) {
            return 1; // 默认启用
        }
        String trimmed = statusStr.trim();
        if ("启用".equals(trimmed) || "1".equals(trimmed)) {
            return 1;
        }
        if ("禁用".equals(trimmed) || "0".equals(trimmed)) {
            return 0;
        }
        try {
            int num = Integer.parseInt(trimmed);
            return num == 0 ? 0 : 1;
        } catch (NumberFormatException e) {
            return 1; // 解析失败默认启用
        }
    }

    /**
     * 解析日期字符串为 LocalDate
     * 
     * 支持多种日期格式：
     * - yyyy-MM-dd
     * - yyyy/MM/dd
     * - yyyy.MM.dd
     * - yyyy年MM月dd日
     * 
     * @param dateStr 日期字符串
     * @param formatters 日期格式化器数组
     * @return LocalDate 对象，解析失败返回 null
     */
    private LocalDate parseLocalDate(String dateStr, DateTimeFormatter[] formatters) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        String trimmed = dateStr.trim();
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
                // 继续尝试下一个格式
            }
        }
        return null; // 所有格式都解析失败
    }
}
