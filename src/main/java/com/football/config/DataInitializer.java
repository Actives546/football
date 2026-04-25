package com.football.config;

import cn.hutool.crypto.digest.BCrypt;
import com.football.entity.User;
import com.football.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {
        // 初始化管理员用户
        initAdminUser();
        // 初始化测试用户
        initTestUser();
    }

    private void initAdminUser() {
        // 检查管理员用户是否已存在
        User existingUser = userMapper.selectByUsername("admin");
        if (existingUser != null) {
            log.info("管理员用户已存在，跳过初始化");
            return;
        }

        // 创建管理员用户
        User admin = new User();
        admin.setUsername("admin");
        admin.setPhone("13800138000");
        // 使用BCrypt加密密码 123456
        String encodedPassword = BCrypt.hashpw("123456", BCrypt.gensalt());
        admin.setPassword(encodedPassword);
        admin.setNickname("管理员");
        admin.setEmail("admin@football.com");
        admin.setStatus(1);

        userMapper.insert(admin);
        log.info("管理员用户初始化成功！用户名：admin，密码：123456");
        log.info("BCrypt密码哈希：{}", encodedPassword);
    }

    private void initTestUser() {
        // 检查测试用户是否已存在
        User existingUser = userMapper.selectByUsername("testuser");
        if (existingUser != null) {
            log.info("测试用户已存在，跳过初始化");
            return;
        }

        // 创建测试用户
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPhone("13800138001");
        // 使用BCrypt加密密码 123456
        String encodedPassword = BCrypt.hashpw("123456", BCrypt.gensalt());
        testUser.setPassword(encodedPassword);
        testUser.setNickname("测试用户");
        testUser.setEmail("test@football.com");
        testUser.setStatus(1);

        userMapper.insert(testUser);
        log.info("测试用户初始化成功！用户名：testuser，密码：123456");
    }
}
