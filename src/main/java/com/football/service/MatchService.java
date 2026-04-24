package com.football.service;

import com.football.common.Result;
import com.football.dto.MatchDTO;
import com.football.dto.MatchQueryDTO;
import com.football.vo.MatchVO;

import java.util.List;
import java.util.Map;

public interface MatchService {

    Result<MatchVO> getById(Long id);

    Result<Map<String, Object>> getPage(MatchQueryDTO queryDTO);

    Result<Boolean> add(MatchDTO matchDTO);

    Result<Boolean> update(MatchDTO matchDTO);

    Result<Boolean> delete(Long id);

    Result<Boolean> deleteBatch(List<Long> ids);
}
