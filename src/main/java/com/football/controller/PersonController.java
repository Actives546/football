package com.football.controller;

import com.football.common.Result;
import com.football.dto.PersonDTO;
import com.football.dto.PersonQueryDTO;
import com.football.service.PersonService;
import com.football.vo.PersonVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/{id}")
    public Result<PersonVO> getById(@PathVariable Long id) {
        return personService.getById(id);
    }

    @PostMapping("/page")
    public Result<Map<String, Object>> getPage(@RequestBody PersonQueryDTO queryDTO) {
        return personService.getPage(queryDTO);
    }

    @GetMapping("/org/{orgId}")
    public Result<List<PersonVO>> getByOrgId(@PathVariable Long orgId) {
        return personService.getByOrgId(orgId);
    }

    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody PersonDTO personDTO) {
        return personService.add(personDTO);
    }

    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody PersonDTO personDTO) {
        return personService.update(personDTO);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return personService.delete(id);
    }

    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return personService.deleteBatch(ids);
    }
}
