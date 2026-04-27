package com.football.controller;

import com.football.common.Result;
import com.football.dto.OrganizationDTO;
import com.football.dto.OrganizationQueryDTO;
import com.football.service.OrganizationService;
import com.football.vo.OrganizationVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/{id}")
    public Result<OrganizationVO> getById(@PathVariable Long id) {
        return organizationService.getById(id);
    }

    @GetMapping("/tree")
    public Result<Map<String, Object>> getTree() {
        return organizationService.getTree();
    }

    @GetMapping("/children/{parentId}")
    public Result<List<OrganizationVO>> getChildren(@PathVariable(required = false) Long parentId) {
        return organizationService.getChildren(parentId);
    }

    @GetMapping("/all")
    public Result<List<OrganizationVO>> getAll() {
        return organizationService.getAll();
    }

    @PostMapping("/page")
    public Result<Map<String, Object>> getPage(@RequestBody OrganizationQueryDTO queryDTO) {
        return organizationService.getPage(queryDTO);
    }

    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody OrganizationDTO organizationDTO) {
        return organizationService.add(organizationDTO);
    }

    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody OrganizationDTO organizationDTO) {
        return organizationService.update(organizationDTO);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return organizationService.delete(id);
    }

    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return organizationService.deleteBatch(ids);
    }
}
