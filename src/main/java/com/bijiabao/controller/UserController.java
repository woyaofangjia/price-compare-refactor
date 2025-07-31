package com.bijiabao.controller;

import com.bijiabao.dto.UserDto;
import com.bijiabao.entity.User;
import com.bijiabao.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class UserController {
    
    private final UserService userService;
    
    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody User user) {
        log.info("创建用户请求: {}", user.getUsername());
        
        try {
            UserDto createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取用户列表
     */
    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("获取用户列表 - 页码: {}, 大小: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDto> users = userService.findAll(pageable);
        
        return ResponseEntity.ok(users);
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("获取用户信息: {}", id);
        
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("更新用户信息: {}", id);
        
        try {
            UserDto updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户: {}", id);
        
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("搜索用户: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDto> users = userService.searchUsers(keyword, pageable);
        
        return ResponseEntity.ok(users);
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<UserDto> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        Integer status = request.get("status");
        log.info("更新用户状态: {} -> {}", id, status);
        
        try {
            UserDto updatedUser = userService.updateStatus(id, status);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新用户角色
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<UserDto> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String role = request.get("role");
        log.info("更新用户角色: {} -> {}", id, role);
        
        try {
            UserDto updatedUser = userService.updateRole(id, role);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("更新用户角色失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        log.info("获取用户统计信息");
        
        Map<String, Object> stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
} 