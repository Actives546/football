package com.football.service;

import com.football.common.Result;
import java.util.Map;

public interface AuthService {

    Result<Map<String, Object>> sendSmsCode(String phone);

    boolean verifyCode(String phone, String code);
}
