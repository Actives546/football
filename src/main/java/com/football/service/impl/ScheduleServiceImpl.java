package com.football.service.impl;

import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.dto.ScheduleDTO;
import com.football.dto.ScheduleQueryDTO;
import com.football.entity.Schedule;
import com.football.mapper.ScheduleMapper;
import com.football.mapper.SeasonMapper;
import com.football.mapper.StadiumMapper;
import com.football.service.ScheduleService;
import com.football.vo.ScheduleVO;
import com.football.vo.SeasonVO;
import com.football.vo.StadiumVO;
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
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private SeasonMapper seasonMapper;

    @Autowired
    private StadiumMapper stadiumMapper;

    private static final String STATUS_SCHEDULED = "SCHEDULED";
    private static final String STATUS_LIVE = "LIVE";
    private static final String STATUS_FINISHED = "FINISHED";

    @Override
    public Result<ScheduleVO> getById(Long id) {
        if (id == null) {
            throw new BusinessException("赛程ID不能为空");
        }
        ScheduleVO scheduleVO = scheduleMapper.selectById(id);
        if (scheduleVO == null) {
            throw new BusinessException("赛程不存在");
        }
        return Result.success("查询成功", scheduleVO);
    }

    @Override
    public Result<Map<String, Object>> getPage(ScheduleQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(10);
        }

        List<ScheduleVO> scheduleList = scheduleMapper.selectList(queryDTO);
        long total = scheduleMapper.selectCount(queryDTO);

        Map<String, Object> result = new HashMap<>();
        result.put("list", scheduleList);
        result.put("total", total);
        result.put("pageNum", queryDTO.getPageNum());
        result.put("pageSize", queryDTO.getPageSize());

        return Result.success("查询成功", result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(ScheduleDTO scheduleDTO) {
        validateScheduleDTO(scheduleDTO);
        validateSeasonExist(scheduleDTO.getSeasonId());
        validateStadiumExist(scheduleDTO.getStadiumId());

        Schedule schedule = convertToEntity(scheduleDTO);

        if (!StringUtils.hasText(schedule.getStatus())) {
            schedule.setStatus(STATUS_SCHEDULED);
        }

        int rows = scheduleMapper.insert(schedule);
        log.info("新增赛程成功，赛程ID：{}，赛程名称：{}", schedule.getId(), schedule.getScheduleName());

        return Result.success("新增成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(ScheduleDTO scheduleDTO) {
        if (scheduleDTO.getId() == null) {
            throw new BusinessException("赛程ID不能为空");
        }

        ScheduleVO existSchedule = scheduleMapper.selectById(scheduleDTO.getId());
        if (existSchedule == null) {
            throw new BusinessException("赛程不存在");
        }

        validateScheduleDTO(scheduleDTO);
        validateSeasonExist(scheduleDTO.getSeasonId());
        validateStadiumExist(scheduleDTO.getStadiumId());

        Schedule schedule = convertToEntity(scheduleDTO);

        int rows = scheduleMapper.updateById(schedule);
        log.info("更新赛程成功，赛程ID：{}，赛程名称：{}", schedule.getId(), schedule.getScheduleName());

        return Result.success("更新成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        if (id == null) {
            throw new BusinessException("赛程ID不能为空");
        }

        ScheduleVO schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("赛程不存在");
        }

        int rows = scheduleMapper.deleteById(id);
        log.info("删除赛程成功，赛程ID：{}，赛程名称：{}", id, schedule.getScheduleName());

        return Result.success("删除成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的赛程");
        }

        int rows = scheduleMapper.deleteByIds(ids);
        log.info("批量删除赛程成功，删除数量：{}", rows);

        return Result.success("批量删除成功", rows > 0);
    }

    @Override
    public Result<List<ScheduleVO>> getBySeasonId(Long seasonId) {
        if (seasonId == null) {
            throw new BusinessException("赛季ID不能为空");
        }
        List<ScheduleVO> list = scheduleMapper.selectBySeasonId(seasonId);
        return Result.success("查询成功", list);
    }

    private void validateScheduleDTO(ScheduleDTO scheduleDTO) {
        if (!StringUtils.hasText(scheduleDTO.getScheduleName())) {
            throw new BusinessException("赛程名称不能为空");
        }
        if (!StringUtils.hasText(scheduleDTO.getStatus())) {
            throw new BusinessException("赛程状态不能为空");
        }
        if (scheduleDTO.getStartTime() == null) {
            throw new BusinessException("开始时间不能为空");
        }
    }

    private void validateSeasonExist(Long seasonId) {
        SeasonVO season = seasonMapper.selectById(seasonId);
        if (season == null) {
            throw new BusinessException("所属赛季不存在");
        }
    }

    private void validateStadiumExist(Long stadiumId) {
        if (stadiumId == null) {
            throw new BusinessException("比赛场地不能为空");
        }
        StadiumVO stadium = stadiumMapper.selectById(stadiumId);
        if (stadium == null) {
            throw new BusinessException("比赛场地不存在");
        }
        if (stadium.getStatus() != 1) {
            throw new BusinessException("比赛场地已被禁用");
        }
    }

    private Schedule convertToEntity(ScheduleDTO dto) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(dto, schedule);
        return schedule;
    }
}
