package com.football.controller;

import com.football.common.Result;
import com.football.dto.StadiumDTO;
import com.football.dto.StadiumQueryDTO;
import com.football.service.StadiumService;
import com.football.vo.StadiumVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stadium")
public class StadiumController {

    @Autowired
    private StadiumService stadiumService;

    @GetMapping("/{id}")
    public Result<StadiumVO> getById(@PathVariable Long id) {
        return stadiumService.getById(id);
    }

    @GetMapping("/all")
    public Result<List<StadiumVO>> getAll() {
        return stadiumService.getAll();
    }

    @GetMapping("/enabled")
    public Result<List<StadiumVO>> getEnabled() {
        return stadiumService.getEnabled();
    }

    @PostMapping("/page")
    public Result<Map<String, Object>> getPage(@RequestBody StadiumQueryDTO queryDTO) {
        return stadiumService.getPage(queryDTO);
    }

    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody StadiumDTO stadiumDTO) {
        return stadiumService.add(stadiumDTO);
    }

    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody StadiumDTO stadiumDTO) {
        return stadiumService.update(stadiumDTO);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return stadiumService.delete(id);
    }

    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return stadiumService.deleteBatch(ids);
    }

    @PostMapping("/enable")
    public Result<Boolean> enableBatch(@RequestBody List<Long> ids) {
        return stadiumService.updateStatusBatch(ids, 1);
    }

    @PostMapping("/disable")
    public Result<Boolean> disableBatch(@RequestBody List<Long> ids) {
        return stadiumService.updateStatusBatch(ids, 0);
    }
}
