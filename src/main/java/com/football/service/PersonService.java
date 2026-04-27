package com.football.service;

import com.football.common.Result;
import com.football.dto.PersonDTO;
import com.football.dto.PersonQueryDTO;
import com.football.vo.PersonVO;

import java.util.List;
import java.util.Map;

public interface PersonService {

    Result<PersonVO> getById(Long id);

    Result<Map<String, Object>> getPage(PersonQueryDTO queryDTO);

    Result<Boolean> add(PersonDTO personDTO);

    Result<Boolean> update(PersonDTO personDTO);

    Result<Boolean> delete(Long id);

    Result<Boolean> deleteBatch(List<Long> ids);

    Result<List<PersonVO>> getByOrgId(Long orgId);
}
