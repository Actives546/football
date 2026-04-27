package com.football.service.impl;

import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.dto.SeasonDTO;
import com.football.dto.SeasonQueryDTO;
import com.football.entity.Match;
import com.football.entity.Season;
import com.football.mapper.MatchMapper;
import com.football.mapper.SeasonMapper;
import com.football.service.SeasonService;
import com.football.vo.SeasonVO;
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
 * 赛季管理业务实现类
 * 
 * 1. 实现赛季的增删改查等核心业务逻辑
 * 2. 处理赛季与赛事的关联校验
 * 3. 提供分页查询、批量删除等扩展功能
 * 
 * @author system
 * @version 1.0.0
 */
@Slf4j
@Service
public class SeasonServiceImpl implements SeasonService {

    @Autowired
    private SeasonMapper seasonMapper;

    @Autowired
    private MatchMapper matchMapper;

    /** 赛季状态：未开始 */
    private static final String STATUS_SCHEDULED = "SCHEDULED";
    /** 赛季状态：进行中 */
    private static final String STATUS_LIVE = "LIVE";
    /** 赛季状态：已结束 */
    private static final String STATUS_FINISHED = "FINISHED";

    /**
     * 根据ID查询赛季详情
     * 
     * 1. 校验赛季ID不能为空
     * 2. 调用Mapper查询赛季信息
     * 3. 校验查询结果，赛季不存在则抛出异常
     * 
     * @param id 赛季ID
     * @return 赛季详情视图对象
     * @throws BusinessException 当赛季ID为空或赛季不存在时抛出
     */
    @Override
    public Result<SeasonVO> getById(Long id) {
        if (id == null) {
            throw new BusinessException("赛季ID不能为空");
        }
        SeasonVO seasonVO = seasonMapper.selectById(id);
        if (seasonVO == null) {
            throw new BusinessException("赛季不存在");
        }
        return Result.success("查询成功", seasonVO);
    }

    /**
     * 分页查询赛季列表
     * 
     * 1. 校验分页参数，设置默认值
     * 2. 计算偏移量（offset）用于SQL分页
     * 3. 调用Mapper查询列表和总数
     * 4. 组装分页结果返回
     * 
     * @param queryDTO 查询条件DTO
     * @return 分页结果，包含列表、总数、页码、每页数量
     */
    @Override
    public Result<Map<String, Object>> getPage(SeasonQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(10);
        }

        List<SeasonVO> seasonList = seasonMapper.selectList(queryDTO);
        long total = seasonMapper.selectCount(queryDTO);

        Map<String, Object> result = new HashMap<>();
        result.put("list", seasonList);
        result.put("total", total);
        result.put("pageNum", queryDTO.getPageNum());
        result.put("pageSize", queryDTO.getPageSize());

        return Result.success("查询成功", result);
    }

    /**
     * 新增赛季
     * 
     * 1. 校验赛季数据DTO的必填字段
     * 2. 校验所属赛事是否存在
     * 3. 转换DTO为实体对象，设置默认状态
     * 4. 执行插入操作并记录日志
     * 
     * @param seasonDTO 赛季数据DTO
     * @return 新增是否成功
     * @throws BusinessException 当数据校验失败或赛事不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(SeasonDTO seasonDTO) {
        validateSeasonDTO(seasonDTO);
        validateMatchExist(seasonDTO.getMatchId());

        Season season = convertToEntity(seasonDTO);

        if (!StringUtils.hasText(season.getStatus())) {
            season.setStatus(STATUS_SCHEDULED);
        }

        int rows = seasonMapper.insert(season);
        log.info("新增赛季成功，赛季ID：{}，赛季名称：{}", season.getId(), season.getSeasonName());

        return Result.success("新增成功", rows > 0);
    }

    /**
     * 更新赛季信息
     * 
     * 1. 校验赛季ID不能为空
     * 2. 校验赛季是否存在
     * 3. 校验更新数据的必填字段
     * 4. 校验所属赛事是否存在
     * 5. 执行更新操作并记录日志
     * 
     * @param seasonDTO 赛季数据DTO
     * @return 更新是否成功
     * @throws BusinessException 当赛季不存在或数据校验失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(SeasonDTO seasonDTO) {
        if (seasonDTO.getId() == null) {
            throw new BusinessException("赛季ID不能为空");
        }

        SeasonVO existSeason = seasonMapper.selectById(seasonDTO.getId());
        if (existSeason == null) {
            throw new BusinessException("赛季不存在");
        }

        validateSeasonDTO(seasonDTO);
        validateMatchExist(seasonDTO.getMatchId());

        Season season = convertToEntity(seasonDTO);

        int rows = seasonMapper.updateById(season);
        log.info("更新赛季成功，赛季ID：{}，赛季名称：{}", season.getId(), season.getSeasonName());

        return Result.success("更新成功", rows > 0);
    }

    /**
     * 根据ID删除赛季（逻辑删除）
     * 
     * 1. 校验赛季ID不能为空
     * 2. 校验赛季是否存在
     * 3. 执行逻辑删除操作
     * 4. 记录删除日志
     * 
     * @param id 赛季ID
     * @return 删除是否成功
     * @throws BusinessException 当赛季ID为空或赛季不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        if (id == null) {
            throw new BusinessException("赛季ID不能为空");
        }

        SeasonVO season = seasonMapper.selectById(id);
        if (season == null) {
            throw new BusinessException("赛季不存在");
        }

        int rows = seasonMapper.deleteById(id);
        log.info("删除赛季成功，赛季ID：{}，赛季名称：{}", id, season.getSeasonName());

        return Result.success("删除成功", rows > 0);
    }

    /**
     * 批量删除赛季（逻辑删除）
     * 
     * 1. 校验待删除的ID列表不能为空
     * 2. 执行批量逻辑删除操作
     * 3. 记录删除数量日志
     * 
     * @param ids 赛季ID列表
     * @return 删除是否成功
     * @throws BusinessException 当ID列表为空时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的赛季");
        }

        int rows = seasonMapper.deleteByIds(ids);
        log.info("批量删除赛季成功，删除数量：{}", rows);

        return Result.success("批量删除成功", rows > 0);
    }

    /**
     * 根据赛事ID查询赛季列表
     * 
     * 1. 校验赛事ID不能为空
     * 2. 调用Mapper查询该赛事下的所有赛季
     * 3. 返回赛季列表（按开始日期降序排列）
     * 
     * @param matchId 赛事ID
     * @return 该赛事下的赛季列表
     * @throws BusinessException 当赛事ID为空时抛出
     */
    @Override
    public Result<List<SeasonVO>> getByMatchId(Long matchId) {
        if (matchId == null) {
            throw new BusinessException("赛事ID不能为空");
        }
        List<SeasonVO> list = seasonMapper.selectByMatchId(matchId);
        return Result.success("查询成功", list);
    }

    /**
     * 校验赛季数据DTO的必填字段
     * 
     * 1. 校验赛季名称不能为空
     * 2. 校验赛季状态不能为空
     * 3. 校验赛季开始日期不能为空
     * 
     * @param seasonDTO 赛季数据DTO
     * @throws BusinessException 当任一必填字段为空时抛出
     */
    private void validateSeasonDTO(SeasonDTO seasonDTO) {
        if (!StringUtils.hasText(seasonDTO.getSeasonName())) {
            throw new BusinessException("赛季名称不能为空");
        }
        if (!StringUtils.hasText(seasonDTO.getStatus())) {
            throw new BusinessException("赛季状态不能为空");
        }
        if (seasonDTO.getStartDate() == null) {
            throw new BusinessException("赛季开始日期不能为空");
        }
    }

    /**
     * 校验赛事是否存在
     * 
     * 1. 根据赛事ID查询赛事信息
     * 2. 校验查询结果，赛事不存在则抛出异常
     * 
     * @param matchId 赛事ID
     * @throws BusinessException 当赛事不存在时抛出
     */
    private void validateMatchExist(Long matchId) {
        Match match = matchMapper.selectById(matchId);
        if (match == null) {
            throw new BusinessException("所属赛事不存在");
        }
    }

    /**
     * 将赛季DTO转换为实体对象
     * 
     * 1. 创建Season实体对象
     * 2. 使用BeanUtils复制属性
     * 3. 返回转换后的实体对象
     * 
     * @param dto 赛季数据DTO
     * @return 赛季实体对象
     */
    private Season convertToEntity(SeasonDTO dto) {
        Season season = new Season();
        BeanUtils.copyProperties(dto, season);
        return season;
    }
}
