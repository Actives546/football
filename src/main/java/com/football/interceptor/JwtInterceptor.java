package com.football.interceptor;

import com.football.common.BusinessException;
import com.football.context.UserContext;
import com.football.util.JwtUtil;
import com.football.util.RedisUtil;
import com.football.vo.UserInfo;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = extractToken(request);
        
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "未携带Token，请先登录");
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            String tokenKey = jwtUtil.getTokenKey(userId);
            Object cachedToken = redisUtil.get(tokenKey);
            if (cachedToken == null || !cachedToken.toString().equals(token)) {
                throw new BusinessException(401, "Token已失效，请重新登录");
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setUsername(jwtUtil.getUsernameFromToken(token));
            userInfo.setPhone(jwtUtil.getPhoneFromToken(token));
            
            UserContext.setUser(userInfo);
            log.debug("用户已认证，userId: {}, username: {}", userId, userInfo.getUsername());
            
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw new BusinessException(401, "Token已过期，请重新登录");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            throw new BusinessException(401, "无效的Token");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
        log.debug("清理用户上下文完成");
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
