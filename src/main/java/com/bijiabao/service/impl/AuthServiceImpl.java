package com.bijiabao.service.impl;

import com.bijiabao.dto.AuthResponse;
import com.bijiabao.dto.LoginRequest;
import com.bijiabao.dto.RegisterRequest;
import com.bijiabao.dto.UserDto;
import com.bijiabao.entity.User;
import com.bijiabao.entity.User.UserStatus;
import com.bijiabao.repository.UserRepository;
import com.bijiabao.service.AuthService;
import com.bijiabao.service.UserService;
import com.bijiabao.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("用户登录: {}", loginRequest.getUsername());
        
        try {
            // 查找用户
            Optional<User> userOpt = userRepository.findByUsernameOrEmail(
                    loginRequest.getUsername(), 
                    loginRequest.getUsername()
            );
            
            if (userOpt.isEmpty()) {
                return AuthResponse.builder()
                        .message("用户名或密码错误")
                        .build();
            }
            
            User user = userOpt.get();
            
            // 验证密码
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return AuthResponse.builder()
                        .message("用户名或密码错误")
                        .build();
            }
            
            // 检查用户状态
            if (user.getStatus() != UserStatus.ACTIVE) {
                return AuthResponse.builder()
                        .message("账户已被禁用")
                        .build();
            }
            
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
            
            // 转换为DTO
            UserDto userDto = convertToDto(user);
            
            return AuthResponse.builder()
                    .token(token)
                    .user(userDto)
                    .message("登录成功")
                    .build();
                    
        } catch (Exception e) {
            log.error("登录失败", e);
            return AuthResponse.builder()
                    .message("登录失败: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("用户注册: {}", registerRequest.getUsername());
        
        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return AuthResponse.builder()
                        .message("用户名已存在")
                        .build();
            }
            
            // 检查邮箱是否已存在
            if (userService.existsByEmail(registerRequest.getEmail())) {
                return AuthResponse.builder()
                        .message("邮箱已存在")
                        .build();
            }
            
            // 创建用户
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setNickname(registerRequest.getNickname());
            user.setRole("user");
            user.setStatus(UserStatus.ACTIVE);
            
            UserDto createdUser = userService.createUser(user);
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
            
            return AuthResponse.builder()
                    .token(token)
                    .user(createdUser)
                    .message("注册成功")
                    .build();
                    
        } catch (Exception e) {
            log.error("注册失败", e);
            return AuthResponse.builder()
                    .message("注册失败: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }
            
            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            String username = jwtUtil.getUsernameFromToken(token);
            return jwtUtil.validateToken(token, username);
            
        } catch (Exception e) {
            log.error("令牌验证失败", e);
            return false;
        }
    }
    
    @Override
    public AuthResponse refreshToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return AuthResponse.builder()
                        .message("无效的令牌")
                        .build();
            }
            
            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 验证令牌
            if (!validateToken(token)) {
                return AuthResponse.builder()
                        .message("令牌已过期或无效")
                        .build();
            }
            
            // 获取用户信息
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            // 查找用户
            Optional<UserDto> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                return AuthResponse.builder()
                        .message("用户不存在")
                        .build();
            }
            
            // 生成新令牌
            String newToken = jwtUtil.generateToken(username, userId, role);
            
            return AuthResponse.builder()
                    .token(newToken)
                    .user(userOpt.get())
                    .message("令牌刷新成功")
                    .build();
                    
        } catch (Exception e) {
            log.error("令牌刷新失败", e);
            return AuthResponse.builder()
                    .message("令牌刷新失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 转换为DTO
     */
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .status(user.getStatus())
                .role(user.getRole())
                .lastLoginTime(user.getLastLoginTime())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}