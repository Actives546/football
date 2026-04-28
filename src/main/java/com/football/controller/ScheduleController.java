package com.football.controller;

import com.football.common.Result;
import com.football.dto.ScheduleDTO;
import com.football.dto.ScheduleQueryDTO;
import com.football.service.ScheduleService;
import com.football.vo.ScheduleVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/{id}")
    public Result<ScheduleVO> getById(@PathVariable Long id) {
        return scheduleService.getById(id);
    }

    @GetMapping("/season/{seasonId}")
    public Result<List<ScheduleVO>> getBySeasonId(@PathVariable Long seasonId) {
        return scheduleService.getBySeasonId(seasonId);
    }

    @PostMapping("/page")
    public Result<Map<String, Object>> getPage(@RequestBody ScheduleQueryDTO queryDTO) {
        return scheduleService.getPage(queryDTO);
    }

    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody ScheduleDTO scheduleDTO) {
        return scheduleService.add(scheduleDTO);
    }

    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody ScheduleDTO scheduleDTO) {
        return scheduleService.update(scheduleDTO);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return scheduleService.delete(id);
    }

    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return scheduleService.deleteBatch(ids);
    }
}
