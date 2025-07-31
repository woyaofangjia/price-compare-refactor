package com.bijiabao.repository;

import com.bijiabao.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日志Repository接口
 */
@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    
    /**
     * 根据用户ID查找日志
     */
    Page<Log> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据操作类型查找日志
     */
    Page<Log> findByAction(String action, Pageable pageable);
    
    /**
     * 根据时间范围查找日志
     */
    Page<Log> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据用户ID和时间范围查找日志
     */
    Page<Log> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据响应状态查找日志
     */
    Page<Log> findByResponseStatus(Integer responseStatus, Pageable pageable);
    
    /**
     * 搜索日志
     */
    @Query("SELECT l FROM Log l WHERE l.action LIKE %:keyword% OR l.description LIKE %:keyword% OR l.path LIKE %:keyword%")
    Page<Log> searchLogs(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 获取日志统计信息
     */
    @Query("SELECT COUNT(l) as total, COUNT(CASE WHEN l.responseStatus >= 400 THEN 1 END) as errors, AVG(l.executionTime) as avgTime FROM Log l")
    Object getLogStats();
    
    /**
     * 获取操作类型统计
     */
    @Query("SELECT l.action, COUNT(l) FROM Log l GROUP BY l.action ORDER BY COUNT(l) DESC")
    List<Object[]> getActionStats();
    
    /**
     * 获取用户操作统计
     */
    @Query("SELECT l.userId, COUNT(l) FROM Log l WHERE l.userId IS NOT NULL GROUP BY l.userId ORDER BY COUNT(l) DESC")
    List<Object[]> getUserActionStats();
} 