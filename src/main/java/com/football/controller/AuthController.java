package com.football.controller;

import com.football.common.Result;
import com.football.dto.LoginDTO;
import com.football.dto.SendCodeDTO;
import com.football.service.AuthService;
import com.football.vo.LoginVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/sendCode")
    public Result<Map<String, Object>> sendCode(@Valid @RequestBody SendCodeDTO dto) {
        // 只保留校验功能，其他逻辑都放到Service中
        return authService.sendSmsCode(dto.getPhone());
    }

    @PostMapping("/verifyCode")
    public Result<Boolean> verifyCode(@RequestParam String phone, @RequestParam String code) {
        boolean result = authService.verifyCode(phone, code);
        return Result.success("验证成功", result);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 调用Service层的登录方法
        return authService.login(loginDTO);
    }
}
