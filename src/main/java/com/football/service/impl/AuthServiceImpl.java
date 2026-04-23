package com.football.service.impl;

import com.football.common.BusinessException;
import com.football.service.AuthService;
import com.football.util.RedisUtil;
import com.football.util.VerifyCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
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
    public void sendSmsCode(String phone) {
        String code = verifyCodeUtil.generateCode(codeLength);
        String key = CODE_PREFIX + phone;

        redisUtil.set(key, code, expireTime, TimeUnit.SECONDS);
        verifyCodeUtil.sendSmsCode(phone, code);

        log.info("短信验证码已发送到 {}，有效期 {} 秒", phone, expireTime);
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
