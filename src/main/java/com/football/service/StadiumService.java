package com.football.service;

import com.football.common.Result;
import com.football.dto.StadiumDTO;
import com.football.dto.StadiumQueryDTO;
import com.football.vo.StadiumVO;

import java.util.List;
import java.util.Map;

public interface StadiumService {

    Result<StadiumVO> getById(Long id);

    Result<Map<String, Object>> getPage(StadiumQueryDTO queryDTO);

    Result<Boolean> add(StadiumDTO stadiumDTO);

    Result<Boolean> update(StadiumDTO stadiumDTO);

    Result<Boolean> delete(Long id);

    Result<Boolean> deleteBatch(List<Long> ids);

    Result<List<StadiumVO>> getAll();

    Result<List<StadiumVO>> getEnabled();

    Result<Boolean> updateStatusBatch(List<Long> ids, Integer status);
}
