package com.football.controller;

import com.football.common.Result;
import com.football.dto.SeasonDTO;
import com.football.dto.SeasonQueryDTO;
import com.football.service.SeasonService;
import com.football.vo.SeasonVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/season")
public class SeasonController {

    @Autowired
    private SeasonService seasonService;

    @GetMapping("/{id}")
    public Result<SeasonVO> getById(@PathVariable Long id) {
        return seasonService.getById(id);
    }

    @GetMapping("/match/{matchId}")
    public Result<List<SeasonVO>> getByMatchId(@PathVariable Long matchId) {
        return seasonService.getByMatchId(matchId);
    }

    @PostMapping("/page")
    public Result<Map<String, Object>> getPage(@RequestBody SeasonQueryDTO queryDTO) {
        return seasonService.getPage(queryDTO);
    }

    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody SeasonDTO seasonDTO) {
        return seasonService.add(seasonDTO);
    }

    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody SeasonDTO seasonDTO) {
        return seasonService.update(seasonDTO);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return seasonService.delete(id);
    }

    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return seasonService.deleteBatch(ids);
    }
}
