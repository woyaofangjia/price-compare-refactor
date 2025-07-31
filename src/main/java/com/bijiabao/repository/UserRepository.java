package com.bijiabao.repository;

import com.bijiabao.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户Repository接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户名或邮箱查找用户
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据状态查找用户
     */
    Page<User> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据角色查找用户
     */
    Page<User> findByRole(String role, Pageable pageable);
    
    /**
     * 搜索用户
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.nickname LIKE %:keyword% OR u.email LIKE %:keyword%")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 获取用户统计信息
     */
    @Query("SELECT COUNT(u) as total, COUNT(CASE WHEN u.status = 'ACTIVE' THEN 1 END) as active, COUNT(CASE WHEN u.isAdmin = true THEN 1 END) as admin FROM User u")
    Object getUserStats();
}