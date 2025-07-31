package com.bijiabao.service;

import com.bijiabao.dto.AuthResponse;
import com.bijiabao.dto.LoginRequest;
import com.bijiabao.dto.RegisterRequest;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    AuthResponse login(LoginRequest loginRequest);
    
    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest registerRequest);
    
    /**
     * 验证令牌
     */
    boolean validateToken(String token);
    
    /**
     * 刷新令牌
     */
    AuthResponse refreshToken(String token);
} 