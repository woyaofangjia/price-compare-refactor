package com.bijiabao.service;

import com.bijiabao.dto.UserDto;
import com.bijiabao.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 创建用户
     */
    UserDto createUser(User user);
    
    /**
     * 根据ID查找用户
     */
    Optional<UserDto> findById(Long id);
    
    /**
     * 根据用户名查找用户
     */
    Optional<UserDto> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<UserDto> findByEmail(String email);
    
    /**
     * 更新用户信息
     */
    UserDto updateUser(Long id, User user);
    
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    
    /**
     * 分页查询用户
     */
    Page<UserDto> findAll(Pageable pageable);
    
    /**
     * 搜索用户
     */
    Page<UserDto> searchUsers(String keyword, Pageable pageable);
    
    /**
     * 更新用户状态
     */
    UserDto updateStatus(Long id, Integer status);
    
    /**
     * 更新用户角色
     */
    UserDto updateRole(Long id, String role);
    
    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStats();
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
} 