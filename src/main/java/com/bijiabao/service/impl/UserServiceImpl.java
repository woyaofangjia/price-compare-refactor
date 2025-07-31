package com.bijiabao.service.impl;

import com.bijiabao.dto.UserDto;
import com.bijiabao.entity.User;
import com.bijiabao.entity.User.UserStatus;
import com.bijiabao.repository.UserRepository;
import com.bijiabao.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDto createUser(User user) {
        log.info("创建用户: {}", user.getUsername());
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认值
        if (user.getNickname() == null) {
            user.setNickname(user.getUsername());
        }
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToDto);
    }
    
    @Override
    public UserDto updateUser(Long id, User userDetails) {
        log.info("更新用户信息: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 更新字段
        if (userDetails.getNickname() != null) {
            user.setNickname(userDetails.getNickname());
        }
        if (userDetails.getAvatar() != null) {
            user.setAvatar(userDetails.getAvatar());
        }
        if (userDetails.getPhone() != null) {
            user.setPhone(userDetails.getPhone());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        log.info("删除用户: {}", id);
        userRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable).map(this::convertToDto);
    }
    
    @Override
    public UserDto updateStatus(Long id, Integer status) {
        log.info("更新用户状态: {} -> {}", id, status);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setStatus(UserStatus.fromCode(status));
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    @Override
    public UserDto updateRole(Long id, String role) {
        log.info("更新用户角色: {} -> {}", id, role);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats() {
        Object[] stats = (Object[]) userRepository.getUserStats();
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", stats[0]);
        result.put("active", stats[1]);
        result.put("admin", stats[2]);
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
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