package com.example.campbooking.g.security;

import com.example.campbooking.common.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * G 模块专用：从 Spring Security 上下文取当前登录用户 ID。
 * 写操作统一从此处取 userId，禁止前端传入。
 */
public class SecurityUtils {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long)) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        return (Long) auth.getPrincipal();
    }
}
