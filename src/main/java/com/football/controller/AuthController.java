package com.football.controller;

import com.football.common.Result;
import com.football.dto.SendCodeDTO;
import com.football.service.AuthService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/sendCode")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeDTO dto) {
        authService.sendSmsCode(dto.getPhone());
        return Result.success("验证码发送成功", null);
    }

    @PostMapping("/verifyCode")
    public Result<Boolean> verifyCode(@RequestParam String phone, @RequestParam String code) {
        boolean result = authService.verifyCode(phone, code);
        return Result.success("验证成功", result);
    }
}
