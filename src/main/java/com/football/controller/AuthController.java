package com.football.controller;

import com.football.common.Result;
import com.football.dto.SendCodeDTO;
import com.football.service.AuthService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

// 1. 声明为 REST 控制器，处理 HTTP 请求
@RestController
// 2. 定义接口基础路径为 /api/auth
@RequestMapping("/api/auth")
public class AuthController {

    // 3. 自动注入认证服务
    @Autowired
    private AuthService authService;

    // 4. 处理发送验证码的 POST 请求
    @PostMapping("/sendCode")
    public Result<Map<String, Object>> sendCode(@Valid @RequestBody SendCodeDTO dto) {
        // 5. 调用认证服务发送短信验证码，并获取生成的验证码
        String code = authService.sendSmsCode(dto.getPhone());
        
        // 6. 创建返回数据的 Map 集合
        Map<String, Object> data = new HashMap<>();
        // 7. 将手机号放入返回数据中
        data.put("phone", dto.getPhone());
        // 8. 将验证码放入返回数据中（开发环境调试用，生产环境应移除）
        data.put("code", code);
        
        // 9. 返回成功响应，包含提示消息和数据
        return Result.success("验证码发送成功", data);
    }

    // 10. 处理验证验证码的 POST 请求
    @PostMapping("/verifyCode")
    public Result<Boolean> verifyCode(@RequestParam String phone, @RequestParam String code) {
        // 11. 调用认证服务验证手机号和验证码
        boolean result = authService.verifyCode(phone, code);
        // 12. 返回验证结果
        return Result.success("验证成功", result);
    }
}
