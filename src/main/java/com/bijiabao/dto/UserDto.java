package com.bijiabao.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import com.bijiabao.entity.User.UserStatus;

/**
 * 用户数据传输对象
 */
@Data
@Builder
public class UserDto {
    
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String phone;
    private UserStatus status;
    private String role;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 