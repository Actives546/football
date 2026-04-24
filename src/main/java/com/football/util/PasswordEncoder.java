package com.football.util;

import cn.hutool.crypto.digest.BCrypt;

public class PasswordEncoder {

    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        String password = "123456";
        String encodedPassword = encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt加密后: " + encodedPassword);
        System.out.println("验证结果: " + matches(password, encodedPassword));
        
        // 生成多个用于测试
        System.out.println("\n--- 其他测试密码 ---");
        System.out.println("admin123: " + encode("admin123"));
        System.out.println("test123: " + encode("test123"));
    }
}
