package com.football.controller;

import com.football.common.Result;
import com.football.dto.NewsDTO;
import com.football.dto.NewsQueryDTO;
import com.football.service.NewsService;
import com.football.vo.NewsVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/{id}")
    public Result<NewsVO> getById(@PathVariable Long id) {

        return newsService.getById(id);
    }

    @PostMapping("/page")
    public Result<Map<String, Object>> getPage(@RequestBody NewsQueryDTO queryDTO) {
        return newsService.getPage(queryDTO);
    }

    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody NewsDTO newsDTO) {
        return newsService.add(newsDTO);
    }

    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody NewsDTO newsDTO) {
        return newsService.update(newsDTO);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return newsService.delete(id);
    }

    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return newsService.deleteBatch(ids);
    }

    @PutMapping("/status/{id}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return newsService.updateStatus(id, status);
    }

    @PutMapping("/status/batch")
    public Result<Boolean> updateStatusBatch(@RequestParam String status, @RequestBody List<Long> ids) {
        return newsService.updateStatusBatch(ids, status);
    }
}
