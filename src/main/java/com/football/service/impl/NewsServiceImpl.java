package com.football.service.impl;

import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.dto.NewsDTO;
import com.football.dto.NewsQueryDTO;
import com.football.entity.News;
import com.football.mapper.NewsMapper;
import com.football.service.NewsService;
import com.football.vo.NewsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    private static final List<String> VALID_CATEGORIES = Arrays.asList("赛事新闻", "官方通知", "精彩回顾", "说说");
    private static final List<String> VALID_STATUSES = Arrays.asList("草稿", "已发布", "已下架");

    @Override
    public Result<NewsVO> getById(Long id) {
        if (id == null) {
            throw new BusinessException("新闻ID不能为空");
        }
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException("新闻不存在");
        }
        NewsVO newsVO = convertToVO(news);
        return Result.success("查询成功", newsVO);
    }

    @Override
    public Result<Map<String, Object>> getPage(NewsQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }

        int limitedPageSize = queryDTO.getPageSize();
        int resultPageSize = limitedPageSize;

        List<News> newsList = newsMapper.selectList(queryDTO);
        long total = newsMapper.selectCount(queryDTO);

        List<NewsVO> voList = newsList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", voList);
        result.put("total", total);
        result.put("pageNum", queryDTO.getPageNum());
        result.put("pageSize", resultPageSize);

        return Result.success("查询成功", result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> add(NewsDTO newsDTO) {
        validateNewsDTO(newsDTO);

        News news = convertToEntity(newsDTO);

        int rows = newsMapper.insert(news);
        log.info("新增新闻成功，新闻ID：{}，新闻标题：{}", news.getId(), news.getTitle());

        return Result.success("新增成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> update(NewsDTO newsDTO) {
        if (newsDTO.getId() == null) {
            throw new BusinessException("新闻ID不能为空");
        }

        News existNews = newsMapper.selectById(newsDTO.getId());
        if (existNews == null) {
            throw new BusinessException("新闻不存在");
        }

        validateNewsDTO(newsDTO);

        News news = convertToEntity(newsDTO);

        int rows = newsMapper.updateById(news);
        log.info("更新新闻成功，新闻ID：{}，新闻标题：{}", news.getId(), news.getTitle());

        return Result.success("更新成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(Long id) {
        if (id == null) {
            throw new BusinessException("新闻ID不能为空");
        }

        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException("新闻不存在");
        }

        int rows = newsMapper.deleteById(id);
        log.info("删除新闻成功，新闻ID：{}，新闻标题：{}", id, news.getTitle());

        return Result.success("删除成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要删除的新闻");
        }

        int rows = newsMapper.deleteByIds(ids);
        log.info("批量删除新闻成功，删除数量：{}", rows);

        return Result.success("批量删除成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateStatus(Long id, String status) {
        if (id == null) {
            throw new BusinessException("新闻ID不能为空");
        }

        if (!VALID_STATUSES.contains(status)) {
            throw new BusinessException("无效的发布状态");
        }

        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException("新闻不存在");
        }

        int rows = newsMapper.updateStatus(id, status);
        log.info("更新新闻状态成功，新闻ID：{}，状态：{}", id, status);

        return Result.success("状态更新成功", rows > 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateStatusBatch(List<Long> ids, String status) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException("请选择要操作的新闻");
        }

        if (!VALID_STATUSES.contains(status)) {
            throw new BusinessException("无效的发布状态");
        }

        int rows = newsMapper.updateStatusBatch(ids, status);
        log.info("批量更新新闻状态成功，数量：{}，状态：{}", rows, status);

        return Result.success("批量状态更新成功", rows > 0);
    }

    private void validateNewsDTO(NewsDTO newsDTO) {
        if (!StringUtils.hasText(newsDTO.getTitle())) {
            throw new BusinessException("新闻标题不能为空");
        }
        if (!StringUtils.hasText(newsDTO.getCategory())) {
            throw new BusinessException("新闻分类不能为空");
        }
        if (!VALID_CATEGORIES.contains(newsDTO.getCategory())) {
            throw new BusinessException("无效的新闻分类");
        }
        if (!StringUtils.hasText(newsDTO.getStatus())) {
            throw new BusinessException("发布状态不能为空");
        }
        if (!VALID_STATUSES.contains(newsDTO.getStatus())) {
            throw new BusinessException("无效的发布状态");
        }
    }

    private News convertToEntity(NewsDTO dto) {
        News news = new News();
        BeanUtils.copyProperties(dto, news);
        return news;
    }

    private NewsVO convertToVO(News news) {
        NewsVO vo = new NewsVO();
        BeanUtils.copyProperties(news, vo);
        return vo;
    }
}
