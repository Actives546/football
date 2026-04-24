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
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeasonServiceImpl implements SeasonService {

    @Autowired
    private SeasonMapper seasonMapper;

    @Autowired
    private MatchMapper matchMapper;

    private static final String STATUS_SCHEDULED = "SCHEDULED";
    private static final String STATUS_LIVE = "LIVE";
    private static final String STATUS_FINISHED = "FINISHED";

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(SeasonDTO seasonDTO) {
        validateSeasonDTO(seasonDTO);
        validateMatchExist(seasonDTO.getMatchId());

        Season season = convertToEntity(seasonDTO);

        if (!StringUtils.hasText(season.getStatus())) {
            season.setStatus(STATUS_SCHEDULED);
        }
        if (season.getCurrentRound() == null) {
            season.setCurrentRound(0);
        }

        int rows = seasonMapper.insert(season);
        log.info("新增赛季成功，赛季ID：{}，赛季名称：{}", season.getId(), season.getSeasonName());

        return Result.success("新增成功", rows > 0);
    }

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

    @Override
    public Result<List<SeasonVO>> getByMatchId(Long matchId) {
        if (matchId == null) {
            throw new BusinessException("赛事ID不能为空");
        }
        List<SeasonVO> list = seasonMapper.selectByMatchId(matchId);
        return Result.success("查询成功", list);
    }

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

    private void validateMatchExist(Long matchId) {
        Match match = matchMapper.selectById(matchId);
        if (match == null) {
            throw new BusinessException("所属赛事不存在");
        }
    }

    private Season convertToEntity(SeasonDTO dto) {
        Season season = new Season();
        BeanUtils.copyProperties(dto, season);
        return season;
    }
}
