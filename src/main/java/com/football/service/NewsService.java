package com.football.service;

import com.football.common.Result;
import com.football.dto.NewsDTO;
import com.football.dto.NewsQueryDTO;
import com.football.vo.NewsVO;

import java.util.List;
import java.util.Map;

public interface NewsService {

    Result<NewsVO> getById(Long id);

    Result<Map<String, Object>> getPage(NewsQueryDTO queryDTO);

    Result<Boolean> add(NewsDTO newsDTO);

    Result<Boolean> update(NewsDTO newsDTO);

    Result<Boolean> delete(Long id);

    Result<Boolean> deleteBatch(List<Long> ids);

    Result<Boolean> updateStatus(Long id, String status);

    Result<Boolean> updateStatusBatch(List<Long> ids, String status);
}
