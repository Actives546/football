package com.football.service;

import com.football.common.Result;
import com.football.dto.ScheduleDTO;
import com.football.dto.ScheduleQueryDTO;
import com.football.vo.ScheduleVO;

import java.util.List;
import java.util.Map;

public interface ScheduleService {

    Result<ScheduleVO> getById(Long id);

    Result<Map<String, Object>> getPage(ScheduleQueryDTO queryDTO);

    Result<Boolean> add(ScheduleDTO scheduleDTO);

    Result<Boolean> update(ScheduleDTO scheduleDTO);

    Result<Boolean> delete(Long id);

    Result<Boolean> deleteBatch(List<Long> ids);

    Result<List<ScheduleVO>> getBySeasonId(Long seasonId);
}
