package com.football.controller;

import com.football.common.Result;
import com.football.dto.MatchDTO;
import com.football.dto.MatchQueryDTO;
import com.football.service.MatchService;
import com.football.vo.MatchVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/{id}")
    public Result<MatchVO> getById(@PathVariable Long id) {
        return matchService.getById(id);
    }

    @PostMapping("/page")
    public Result<Map<String, Object>> getPage(@RequestBody MatchQueryDTO queryDTO) {
        return matchService.getPage(queryDTO);
    }

    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody MatchDTO matchDTO) {
        return matchService.add(matchDTO);
    }

    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody MatchDTO matchDTO) {
        return matchService.update(matchDTO);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return matchService.delete(id);
    }

    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return matchService.deleteBatch(ids);
    }
}
