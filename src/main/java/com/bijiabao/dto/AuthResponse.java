package com.bijiabao.dto;

import lombok.Builder;
import lombok.Data;
/**
 * 认证响应DTO
 */
@Data
@Builder
public class AuthResponse {
    private String token;
    private UserDto user;
    private String username;
    private String message;
}