package com.bijiabao.service;

import com.bijiabao.dto.LogDto;
import com.bijiabao.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 日志服务接口
 */
public interface LogService {
    
    /**
     * 创建日志
     */
    LogDto createLog(Log log);
    
    /**
     * 根据ID查找日志
     */
    LogDto findById(Long id);
    
    /**
     * 分页查询日志
     */
    Page<LogDto> findAll(Pageable pageable);
    
    /**
     * 根据用户ID查询日志
     */
    Page<LogDto> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据操作类型查询日志
     */
    Page<LogDto> findByAction(String action, Pageable pageable);
    
    /**
     * 根据时间范围查询日志
     */
    Page<LogDto> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据用户ID和时间范围查询日志
     */
    Page<LogDto> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据响应状态查询日志
     */
    Page<LogDto> findByResponseStatus(Integer responseStatus, Pageable pageable);
    
    /**
     * 搜索日志
     */
    Page<LogDto> searchLogs(String keyword, Pageable pageable);
    
    /**
     * 获取日志统计信息
     */
    Map<String, Object> getLogStats();
    
    /**
     * 获取操作类型统计
     */
    List<Map<String, Object>> getActionStats();
    
    /**
     * 获取用户操作统计
     */
    List<Map<String, Object>> getUserActionStats();
    
    /**
     * 清理过期日志
     */
    void cleanExpiredLogs(LocalDateTime before);
} 