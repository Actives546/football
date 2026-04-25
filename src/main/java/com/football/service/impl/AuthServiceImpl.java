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

// 1. 使用 Slf4j 日志注解，方便记录日志
@Slf4j
// 2. 声明为 Service 组件，由 Spring 容器管理
@Service
public class AuthServiceImpl implements AuthService {

    // 3. 自动注入 Redis 工具类，用于操作 Redis
    @Autowired
    private RedisUtil redisUtil;

    // 4. 自动注入验证码工具类，用于生成和发送验证码
    @Autowired
    private VerifyCodeUtil verifyCodeUtil;

    // 5. 从配置文件读取验证码过期时间，默认 300 秒（5 分钟）
    @Value("${football.verify-code.expire-time:300}")
    private long expireTime;

    // 6. 从配置文件读取验证码长度，默认 6 位
    @Value("${football.verify-code.length:6}")
    private int codeLength;

    // 7. 定义 Redis 中验证码的 key 前缀，用于区分不同类型的缓存
    private static final String CODE_PREFIX = "verify:code:";

    // 8. 实现发送短信验证码的方法，返回生成的验证码
    @Override
    public String sendSmsCode(String phone) {
        // 9. 调用验证码工具类生成指定长度的随机数字验证码
        String code = verifyCodeUtil.generateCode(codeLength);
        // 10. 拼接 Redis 缓存的 key，格式为：verify:code:手机号
        String key = CODE_PREFIX + phone;

        // 11. 将验证码存入 Redis，设置过期时间（单位：秒）
        redisUtil.set(key, code, expireTime, TimeUnit.SECONDS);
        // 12. 调用验证码工具类发送短信验证码（实际项目中这里会调用第三方短信服务）
        verifyCodeUtil.sendSmsCode(phone, code);

        // 13. 记录日志，方便调试和排查问题
        log.info("短信验证码已发送到 {}，验证码：{}，有效期 {} 秒", phone, code, expireTime);
        
        // 14. 返回生成的验证码，供前端使用（仅用于开发环境调试，生产环境不应返回）
        return code;
    }

    // 15. 实现验证验证码的方法
    @Override
    public boolean verifyCode(String phone, String code) {
        // 16. 拼接 Redis 缓存的 key，与存储时使用相同的格式
        String key = CODE_PREFIX + phone;
        // 17. 从 Redis 中获取存储的验证码
        Object cachedCode = redisUtil.get(key);

        // 18. 判断缓存中是否存在该验证码，不存在则抛出异常
        if (cachedCode == null) {
            // 19. 抛出业务异常，提示验证码已过期或不存在
            throw new BusinessException("验证码已过期或不存在");
        }

        // 20. 比较用户输入的验证码与缓存中的验证码是否一致
        if (!cachedCode.toString().equals(code)) {
            // 21. 验证码不一致，抛出业务异常
            throw new BusinessException("验证码错误");
        }

        // 22. 验证成功后，从 Redis 中删除该验证码，防止重复使用
        redisUtil.delete(key);
        // 23. 返回验证成功结果
        return true;
    }
}
