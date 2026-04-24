package com.football.service.impl;

import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.service.AuthService;
import com.football.util.RedisUtil;
import com.football.util.VerifyCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private VerifyCodeUtil verifyCodeUtil;

    @Value("${football.verify-code.expire-time:300}")
    private long expireTime;

    @Value("${football.verify-code.length:6}")
    private int codeLength;

    private static final String CODE_PREFIX = "verify:code:";

    @Override
    public Result<Map<String, Object>> sendSmsCode(String phone) {
        // 1. 调用验证码工具类生成指定长度的随机数字验证码
        String code = verifyCodeUtil.generateCode(codeLength);
        // 2. 拼接 Redis 缓存的 key，格式为：verify:code:手机号
        String key = CODE_PREFIX + phone;

        // 3. 将验证码存入 Redis，设置过期时间（单位：秒）
        redisUtil.set(key, code, expireTime, TimeUnit.SECONDS);
        // 4. 调用验证码工具类发送短信验证码（实际项目中这里会调用第三方短信服务）
        verifyCodeUtil.sendSmsCode(phone, code);

        // 5. 记录日志，方便调试和排查问题
        log.info("短信验证码已发送到 {}，验证码：{}，有效期 {} 秒", phone, code, expireTime);
        
        // 6. 创建返回数据的 Map 集合
        Map<String, Object> data = new HashMap<>();
        // 7. 将手机号放入返回数据中
        data.put("phone", phone);
        // 8. 将验证码放入返回数据中（开发环境调试用，生产环境应移除）
        data.put("code", code);
        
        // 9. 返回成功响应，包含提示消息和数据
        return Result.success("验证码发送成功", data);
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        String key = CODE_PREFIX + phone;
        Object cachedCode = redisUtil.get(key);

        if (cachedCode == null) {
            throw new BusinessException("验证码已过期或不存在");
        }

        if (!cachedCode.toString().equals(code)) {
            throw new BusinessException("验证码错误");
        }

        redisUtil.delete(key);
        return true;
    }
}
