package com.football.service;

import com.football.common.Result;
import com.football.dto.LoginDTO;
import com.football.dto.RegisterDTO;
import com.football.vo.LoginVO;
import java.util.Map;

public interface AuthService {

    Result<Map<String, Object>> sendSmsCode(String phone);

    boolean verifyCode(String phone, String code);

    Result<LoginVO> login(LoginDTO loginDTO);

    Result<Boolean> register(RegisterDTO registerDTO);
}
