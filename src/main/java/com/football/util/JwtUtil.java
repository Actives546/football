package com.football.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    // JWT密钥，从配置文件中读取
    @Value("${football.jwt.secret:football-system-secret-key-2024-very-long-secret-key}")
    private String secret;

    // JWT过期时间（毫秒），默认24小时，从配置文件中读取
    @Value("${football.jwt.expiration:86400000}")
    private long expiration;

    // Redis中存储Token的前缀
    private static final String TOKEN_PREFIX = "user:token:";

    // 生成SecretKey对象
    private SecretKey getSecretKey() {
        // 将字符串密钥转换为字节数组
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // 使用Keys工具类生成SecretKey对象，确保密钥长度符合HS256算法要求
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 根据用户信息生成JWT令牌
    public String generateToken(Long userId, String username, String phone) {
        // 创建载荷（Payload）的自定义声明
        Map<String, Object> claims = new HashMap<>();
        // 将用户ID放入载荷中
        claims.put("userId", userId);
        // 将用户名放入载荷中
        claims.put("username", username);
        // 将手机号放入载荷中
        claims.put("phone", phone);

        // 调用内部方法生成Token
        return createToken(claims, userId.toString());
    }

    // 内部方法：根据声明和主题生成Token
    private String createToken(Map<String, Object> claims, String subject) {
        // 获取当前时间
        Date now = new Date();
        // 计算过期时间
        Date expirationDate = new Date(now.getTime() + expiration);

        // 使用Jwts.builder()构建JWT令牌
        return Jwts.builder()
                // 设置自定义声明（Payload中的数据）
                .setClaims(claims)
                // 设置主题（通常是用户唯一标识）
                .setSubject(subject)
                // 设置签发时间
                .setIssuedAt(now)
                // 设置过期时间
                .setExpiration(expirationDate)
                // 设置签名算法和密钥
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                // 压缩并生成JWT字符串
                .compact();
    }

    // 从Token中获取所有声明（Claims）
    public Claims getClaimsFromToken(String token) {
        try {
            // 使用Jwts.parserBuilder()解析Token
            return Jwts.parserBuilder()
                    // 设置签名密钥，用于验证Token的完整性
                    .setSigningKey(getSecretKey())
                    .build()
                    // 解析Token并获取载荷
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Token已过期
            log.warn("Token已过期: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // Token解析失败（格式错误、签名不匹配等）
            log.error("Token解析失败: {}", e.getMessage());
            throw new RuntimeException("无效的Token");
        }
    }

    // 从Token中获取用户ID
    public Long getUserIdFromToken(String token) {
        // 获取所有声明
        Claims claims = getClaimsFromToken(token);
        // 从声明中获取用户ID
        return claims.get("userId", Long.class);
    }

    // 从Token中获取用户名
    public String getUsernameFromToken(String token) {
        // 获取所有声明
        Claims claims = getClaimsFromToken(token);
        // 从声明中获取用户名
        return claims.get("username", String.class);
    }

    // 从Token中获取手机号
    public String getPhoneFromToken(String token) {
        // 获取所有声明
        Claims claims = getClaimsFromToken(token);
        // 从声明中获取手机号
        return claims.get("phone", String.class);
    }

    // 验证Token是否有效
    public boolean validateToken(String token) {
        try {
            // 尝试解析Token，如果没有异常说明Token有效
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            // 有异常说明Token无效
            log.warn("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    // 检查Token是否已过期
    public boolean isTokenExpired(String token) {
        try {
            // 获取Token中的过期时间
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            // 比较过期时间是否早于当前时间
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            // 解析时已经抛出过期异常，直接返回true
            return true;
        }
    }

    // 生成Redis中存储Token的key
    public String getTokenKey(Long userId) {
        return TOKEN_PREFIX + userId;
    }

    // 获取Token的过期时间（毫秒）
    public long getExpiration() {
        return expiration;
    }
}
