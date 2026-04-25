package com.football.util;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VerifyCodeUtil {

    public String generateCode(int length) {
        return RandomUtil.randomNumbers(length);
    }

    public String generateCode() {
        return generateCode(6);
    }

    public void sendSmsCode(String phone, String code) {
        log.info("发送验证码到手机 {}: {}", phone, code);
    }

    public void sendEmailCode(String email, String code) {
        log.info("发送验证码到邮箱 {}: {}", email, code);
    }
}
