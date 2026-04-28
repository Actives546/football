package com.football.service.impl;

import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.dto.StadiumDTO;
import com.football.dto.StadiumQueryDTO;
import com.football.entity.Stadium;
import com.football.mapper.StadiumMapper;
import com.football.service.StadiumService;
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
public class StadiumServiceImpl implements StadiumService {

    @Autowired
    private StadiumMapper stadiumMapper;

    @Override
    public Result<StadiumVO> getById(Long id) {
        if (id == null) {
            throw new BusinessException("场地ID不能为空");
        }
        StadiumVO stadiumVO = stadiumMapper.selectById(id);
        if (stadiumVO == null) {
            throw new BusinessException("场地不存在");
        }
        stadiumVO.setStatusText(getStatusText(stadiumVO.getStatus()));
        return Result.success("查询成功", stadiumVO);
    }

    @Override
    public Result<Map<String, Object>> getPage(StadiumQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(10);
        }

        List<StadiumVO> stadiumList = stadiumMapper.selectList(queryDTO);
        stadiumList.forEach(stadium -> stadium.setStatusText(getStatusText(stadium.getStatus())));
        long total = stadiumMapper.selectCount(queryDTO);

        Map<String, Object> result = new HashMap<>();
        result.put("list", stadiumList);
        result.put("total", total);
        result.put("pageNum", queryDTO.getPageNum());
        result.put("pageSize", queryDTO.getPageSize());

        return Result.success("查询成功", result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(StadiumDTO stadiumDTO) {
        validateStadiumDTO(stadiumDTO);
        validateStadiumNameUnique(stadiumDTO.getStadiumName(), null);

        Stadium stadium = convertToEntity(stadiumDTO);

        if (stadium.getStatus() == null) {
            stadium.setStatus(1);
        }

        int rows = stadiumMapper.insert(stadium);
        log.info("新增场地成功，场地ID：{}，场地名称：{}", stadium.getId(), stadium.getStadiumName());

        return Result.success("新增成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(StadiumDTO stadiumDTO) {
        if (stadiumDTO.getId() == null) {
            throw new BusinessException("场地ID不能为空");
        }

        StadiumVO existStadium = stadiumMapper.selectById(stadiumDTO.getId());
        if (existStadium == null) {
            throw new BusinessException("场地不存在");
        }

        validateStadiumDTO(stadiumDTO);
        validateStadiumNameUnique(stadiumDTO.getStadiumName(), stadiumDTO.getId());

        Stadium stadium = convertToEntity(stadiumDTO);

        int rows = stadiumMapper.updateById(stadium);
        log.info("更新场地成功，场地ID：{}，场地名称：{}", stadium.getId(), stadium.getStadiumName());

        return Result.success("更新成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        if (id == null) {
            throw new BusinessException("场地ID不能为空");
        }

        StadiumVO stadium = stadiumMapper.selectById(id);
        if (stadium == null) {
            throw new BusinessException("场地不存在");
        }

        int rows = stadiumMapper.deleteById(id);
        log.info("删除场地成功，场地ID：{}，场地名称：{}", id, stadium.getStadiumName());

        return Result.success("删除成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的场地");
        }

        int rows = stadiumMapper.deleteByIds(ids);
        log.info("批量删除场地成功，删除数量：{}", rows);

        return Result.success("批量删除成功", rows > 0);
    }

    @Override
    public Result<List<StadiumVO>> getAll() {
        List<StadiumVO> list = stadiumMapper.selectAll();
        list.forEach(stadium -> stadium.setStatusText(getStatusText(stadium.getStatus())));
        return Result.success("查询成功", list);
    }

    @Override
    public Result<List<StadiumVO>> getEnabled() {
        List<StadiumVO> list = stadiumMapper.selectByStatus(1);
        list.forEach(stadium -> stadium.setStatusText(getStatusText(stadium.getStatus())));
        return Result.success("查询成功", list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateStatusBatch(List<Long> ids, Integer status) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要更新的场地");
        }
        
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值无效");
        }
        
        int successCount = 0;
        for (Long id : ids) {
            Stadium stadium = new Stadium();
            stadium.setId(id);
            stadium.setStatus(status);
            int rows = stadiumMapper.updateById(stadium);
            if (rows > 0) {
                successCount++;
            }
        }
        
        log.info("批量更新场地状态成功，成功数量：{}，目标状态：{}", successCount, getStatusText(status));
        
        return Result.success("批量更新状态成功，成功数量：" + successCount, successCount > 0);
    }

    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "启用" : "禁用";
    }

    private void validateStadiumDTO(StadiumDTO stadiumDTO) {
        if (!StringUtils.hasText(stadiumDTO.getStadiumName())) {
            throw new BusinessException("场地名称不能为空");
        }
    }

    private void validateStadiumNameUnique(String stadiumName, Long excludeId) {
        StadiumVO existStadium = stadiumMapper.selectByName(stadiumName, excludeId);
        if (existStadium != null) {
            throw new BusinessException("场地名称已存在：" + stadiumName);
        }
    }

    private Stadium convertToEntity(StadiumDTO dto) {
        Stadium stadium = new Stadium();
        BeanUtils.copyProperties(dto, stadium);
        return stadium;
    }
}
