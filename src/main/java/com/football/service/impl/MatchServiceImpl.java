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

    @Override
    public Result<MatchVO> getById(Long id) {
        if (id == null) {
            throw new BusinessException("赛事ID不能为空");
        }
        Match match = matchMapper.selectById(id);
        if (match == null) {
            throw new BusinessException("赛事不存在");
        }
        MatchVO matchVO = convertToVO(match);
        return Result.success("查询成功", matchVO);
    }

    @Override
    public Result<Map<String, Object>> getPage(MatchQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(10);
        }

        List<Match> matchList = matchMapper.selectList(queryDTO);
        long total = matchMapper.selectCount(queryDTO);

        List<MatchVO> voList = matchList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", voList);
        result.put("total", total);
        result.put("pageNum", queryDTO.getPageNum());
        result.put("pageSize", queryDTO.getPageSize());

        return Result.success("查询成功", result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(MatchDTO matchDTO) {
        validateMatchDTO(matchDTO);

        Match match = convertToEntity(matchDTO);

        int rows = matchMapper.insert(match);
        log.info("新增赛事成功，赛事ID：{}，赛事名称：{}", match.getId(), match.getMatchName());

        return Result.success("新增成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(MatchDTO matchDTO) {
        if (matchDTO.getId() == null) {
            throw new BusinessException("赛事ID不能为空");
        }

        Match existMatch = matchMapper.selectById(matchDTO.getId());
        if (existMatch == null) {
            throw new BusinessException("赛事不存在");
        }

        validateMatchDTO(matchDTO);

        Match match = convertToEntity(matchDTO);

        int rows = matchMapper.updateById(match);
        log.info("更新赛事成功，赛事ID：{}，赛事名称：{}", match.getId(), match.getMatchName());

        return Result.success("更新成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        if (id == null) {
            throw new BusinessException("赛事ID不能为空");
        }

        Match match = matchMapper.selectById(id);
        if (match == null) {
            throw new BusinessException("赛事不存在");
        }

        int rows = matchMapper.deleteById(id);
        log.info("删除赛事成功，赛事ID：{}，赛事名称：{}", id, match.getMatchName());

        return Result.success("删除成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的赛事");
        }

        int rows = matchMapper.deleteByIds(ids);
        log.info("批量删除赛事成功，删除数量：{}", rows);

        return Result.success("批量删除成功", rows > 0);
    }

    private void validateMatchDTO(MatchDTO matchDTO) {
        if (!StringUtils.hasText(matchDTO.getMatchName())) {
            throw new BusinessException("赛事名称不能为空");
        }
        if (!StringUtils.hasText(matchDTO.getMatchType())) {
            throw new BusinessException("赛事类型不能为空");
        }
    }

    private Match convertToEntity(MatchDTO dto) {
        Match match = new Match();
        BeanUtils.copyProperties(dto, match);
        return match;
    }

    private MatchVO convertToVO(Match match) {
        MatchVO vo = new MatchVO();
        BeanUtils.copyProperties(match, vo);
        return vo;
    }
}
