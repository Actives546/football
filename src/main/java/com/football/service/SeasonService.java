package com.football.service;

import com.football.common.Result;
import com.football.dto.SeasonDTO;
import com.football.dto.SeasonQueryDTO;
import com.football.vo.SeasonVO;

import java.util.List;
import java.util.Map;

public interface SeasonService {

    Result<SeasonVO> getById(Long id);

    Result<Map<String, Object>> getPage(SeasonQueryDTO queryDTO);

    Result<Boolean> add(SeasonDTO seasonDTO);

    Result<Boolean> update(SeasonDTO seasonDTO);

    Result<Boolean> delete(Long id);

    Result<Boolean> deleteBatch(List<Long> ids);

    Result<List<SeasonVO>> getByMatchId(Long matchId);
}
