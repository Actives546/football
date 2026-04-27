package com.football.service.impl;

import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.dto.MatchDTO;
import com.football.dto.MatchQueryDTO;
import com.football.entity.Match;
import com.football.mapper.MatchMapper;
import com.football.service.MatchService;
import com.football.vo.MatchVO;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private MatchMapper matchMapper;

    /**
     * 根据ID查询赛事详情
     * 1. 校验ID参数是否为空
     * 2. 根据ID查询赛事信息
     * 3. 将Entity转换为VO并返回
     *
     * @param id 赛事ID
     * @return 赛事详情VO
     */
    @Override
    public Result<MatchVO> getById(Long id) {
        // 1. 校验ID参数是否为空
        if (id == null) {
            throw new BusinessException("赛事ID不能为空");
        }
        // 2. 根据ID查询赛事信息
        Match match = matchMapper.selectById(id);
        if (match == null) {
            throw new BusinessException("赛事不存在");
        }
        // 3. 将Entity转换为VO并返回
        MatchVO matchVO = convertToVO(match);
        return Result.success("查询成功", matchVO);
    }

    /**
     * 分页查询赛事列表
     * 1. 校验并设置分页参数默认值
     * 2. 判断并限制最大分页数不超过50条
     * 3. 查询赛事列表数据
     * 4. 查询总记录数
     * 5. 转换数据并封装返回结果
     *
     * @param queryDTO 查询条件DTO
     * @return 分页结果Map，包含list、total、pageNum、pageSize
     */
    @Override
    public Result<Map<String, Object>> getPage(MatchQueryDTO queryDTO) {
        // 1. 校验并设置分页参数默认值
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }

        // 2. 判断并限制最大分页数不超过50条
        // 获取经过限制后的分页大小
        int limitedPageSize = queryDTO.getPageSize();
        // 用于返回给前端的分页大小
        int resultPageSize = limitedPageSize;

        // 3. 查询赛事列表数据
        List<Match> matchList = matchMapper.selectList(queryDTO);
        // 4. 查询总记录数
        long total = matchMapper.selectCount(queryDTO);

        // 5. 转换数据并封装返回结果
        List<MatchVO> voList = matchList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", voList);
        result.put("total", total);
        result.put("pageNum", queryDTO.getPageNum());
        result.put("pageSize", resultPageSize);

        return Result.success("查询成功", result);
    }

    /**
     * 新增赛事
     * 1. 校验赛事DTO参数
     * 2. 将DTO转换为Entity
     * 3. 执行插入操作
     * 4. 记录日志并返回结果
     *
     * @param matchDTO 赛事DTO
     * @return 是否新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(MatchDTO matchDTO) {
        // 1. 校验赛事DTO参数
        validateMatchDTO(matchDTO);

        // 2. 将DTO转换为Entity
        Match match = convertToEntity(matchDTO);

        // 3. 执行插入操作
        int rows = matchMapper.insert(match);
        // 4. 记录日志并返回结果
        log.info("新增赛事成功，赛事ID：{}，赛事名称：{}", match.getId(), match.getMatchName());

        return Result.success("新增成功", rows > 0);
    }

    /**
     * 更新赛事
     * 1. 校验ID参数是否为空
     * 2. 校验赛事是否存在
     * 3. 校验赛事DTO参数
     * 4. 将DTO转换为Entity
     * 5. 执行更新操作
     * 6. 记录日志并返回结果
     *
     * @param matchDTO 赛事DTO
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(MatchDTO matchDTO) {
        // 1. 校验ID参数是否为空
        if (matchDTO.getId() == null) {
            throw new BusinessException("赛事ID不能为空");
        }

        // 2. 校验赛事是否存在
        Match existMatch = matchMapper.selectById(matchDTO.getId());
        if (existMatch == null) {
            throw new BusinessException("赛事不存在");
        }

        // 3. 校验赛事DTO参数
        validateMatchDTO(matchDTO);

        // 4. 将DTO转换为Entity
        Match match = convertToEntity(matchDTO);

        // 5. 执行更新操作
        int rows = matchMapper.updateById(match);
        // 6. 记录日志并返回结果
        log.info("更新赛事成功，赛事ID：{}，赛事名称：{}", match.getId(), match.getMatchName());

        return Result.success("更新成功", rows > 0);
    }

    /**
     * 删除赛事（逻辑删除）
     * 1. 校验ID参数是否为空
     * 2. 校验赛事是否存在
     * 3. 执行逻辑删除操作
     * 4. 记录日志并返回结果
     *
     * @param id 赛事ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        // 1. 校验ID参数是否为空
        if (id == null) {
            throw new BusinessException("赛事ID不能为空");
        }

        // 2. 校验赛事是否存在
        Match match = matchMapper.selectById(id);
        if (match == null) {
            throw new BusinessException("赛事不存在");
        }

        // 3. 执行逻辑删除操作
        int rows = matchMapper.deleteById(id);
        // 4. 记录日志并返回结果
        log.info("删除赛事成功，赛事ID：{}，赛事名称：{}", id, match.getMatchName());

        return Result.success("删除成功", rows > 0);
    }

    /**
     * 批量删除赛事（逻辑删除）
     * 1. 校验ID列表是否为空
     * 2. 执行批量逻辑删除操作
     * 3. 记录日志并返回结果
     *
     * @param ids 赛事ID列表
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        // 1. 校验ID列表是否为空
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的赛事");
        }

        // 2. 执行批量逻辑删除操作
        int rows = matchMapper.deleteByIds(ids);
        // 3. 记录日志并返回结果
        log.info("批量删除赛事成功，删除数量：{}", rows);

        return Result.success("批量删除成功", rows > 0);
    }

    /**
     * 校验赛事DTO参数
     * 1. 校验赛事名称是否为空
     * 2. 校验赛事类型是否为空
     *
     * @param matchDTO 赛事DTO
     */
    private void validateMatchDTO(MatchDTO matchDTO) {
        // 1. 校验赛事名称是否为空
        if (!StringUtils.hasText(matchDTO.getMatchName())) {
            throw new BusinessException("赛事名称不能为空");
        }
        // 2. 校验赛事类型是否为空
        if (!StringUtils.hasText(matchDTO.getMatchType())) {
            throw new BusinessException("赛事类型不能为空");
        }
    }

    /**
     * 将MatchDTO转换为Match实体
     * 1. 创建Match实体对象
     * 2. 使用BeanUtils复制属性
     * 3. 返回转换后的实体
     *
     * @param dto 赛事DTO
     * @return Match实体
     */
    private Match convertToEntity(MatchDTO dto) {
        // 1. 创建Match实体对象
        Match match = new Match();
        // 2. 使用BeanUtils复制属性
        BeanUtils.copyProperties(dto, match);
        // 3. 返回转换后的实体
        return match;
    }

    /**
     * 将Match实体转换为MatchVO
     * 1. 创建MatchVO对象
     * 2. 使用BeanUtils复制属性
     * 3. 返回转换后的VO
     *
     * @param match 赛事实体
     * @return MatchVO
     */
    private MatchVO convertToVO(Match match) {
        // 1. 创建MatchVO对象
        MatchVO vo = new MatchVO();
        // 2. 使用BeanUtils复制属性
        BeanUtils.copyProperties(match, vo);
        // 3. 返回转换后的VO
        return vo;
    }
}
