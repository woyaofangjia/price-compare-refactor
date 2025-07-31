package com.bijiabao.controller;

import com.bijiabao.dto.LogDto;
import com.bijiabao.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 日志控制器
 */
@RestController
@RequestMapping("/logs")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class LogController {
    
    private final LogService logService;
    
    public LogController(LogService logService) {
        this.logService = logService;
    }
    
    /**
     * 获取日志列表
     */
    @GetMapping
    public ResponseEntity<Page<LogDto>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LogDto> logs = logService.findAll(pageable);
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 根据ID获取日志
     */
    @GetMapping("/{id}")
    public ResponseEntity<LogDto> getLogById(@PathVariable Long id) {
        LogDto log = logService.findById(id);
        
        if (log != null) {
            return ResponseEntity.ok(log);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 根据用户ID获取日志
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<LogDto>> getLogsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LogDto> logs = logService.findByUserId(userId, pageable);
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 根据操作类型获取日志
     */
    @GetMapping("/action/{action}")
    public ResponseEntity<Page<LogDto>> getLogsByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LogDto> logs = logService.findByAction(action, pageable);
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 根据时间范围获取日志
     */
    @GetMapping("/time-range")
    public ResponseEntity<Page<LogDto>> getLogsByTimeRange(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LogDto> logs = logService.findByTimeRange(start, end, pageable);
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 根据响应状态获取日志
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<LogDto>> getLogsByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LogDto> logs = logService.findByResponseStatus(status, pageable);
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 搜索日志
     */
    @GetMapping("/search")
    public ResponseEntity<Page<LogDto>> searchLogs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LogDto> logs = logService.searchLogs(keyword, pageable);
        
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 获取日志统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getLogStats() {
        Map<String, Object> stats = logService.getLogStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 获取操作类型统计
     */
    @GetMapping("/action-stats")
    public ResponseEntity<List<Map<String, Object>>> getActionStats() {
        List<Map<String, Object>> actionStats = logService.getActionStats();
        return ResponseEntity.ok(actionStats);
    }
    
    /**
     * 获取用户操作统计
     */
    @GetMapping("/user-action-stats")
    public ResponseEntity<List<Map<String, Object>>> getUserActionStats() {
        List<Map<String, Object>> userActionStats = logService.getUserActionStats();
        return ResponseEntity.ok(userActionStats);
    }
} 