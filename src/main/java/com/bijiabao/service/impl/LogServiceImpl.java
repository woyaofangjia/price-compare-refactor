package com.bijiabao.service.impl;

import com.bijiabao.dto.LogDto;
import com.bijiabao.entity.Log;
import com.bijiabao.repository.LogRepository;
import com.bijiabao.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日志服务实现类
 */
@Service
@Transactional
public class LogServiceImpl implements LogService {
    
    private final LogRepository logRepository;
    
    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }
    
    @Override
    public LogDto createLog(Log log) {
        Log savedLog = logRepository.save(log);
        return convertToDto(savedLog);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LogDto findById(Long id) {
        return logRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LogDto> findAll(Pageable pageable) {
        return logRepository.findAll(pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LogDto> findByUserId(Long userId, Pageable pageable) {
        return logRepository.findByUserId(userId, pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LogDto> findByAction(String action, Pageable pageable) {
        return logRepository.findByAction(action, pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LogDto> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return logRepository.findByCreatedAtBetween(startTime, endTime, pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LogDto> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return logRepository.findByUserIdAndCreatedAtBetween(userId, startTime, endTime, pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LogDto> findByResponseStatus(Integer responseStatus, Pageable pageable) {
        return logRepository.findByResponseStatus(responseStatus, pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LogDto> searchLogs(String keyword, Pageable pageable) {
        return logRepository.searchLogs(keyword, pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLogStats() {
        Object[] stats = (Object[]) logRepository.getLogStats();
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", stats[0]);
        result.put("errors", stats[1]);
        result.put("avgTime", stats[2]);
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActionStats() {
        List<Object[]> actionStats = logRepository.getActionStats();
        
        return actionStats.stream().map(stat -> {
            Map<String, Object> action = new HashMap<>();
            action.put("action", stat[0]);
            action.put("count", stat[1]);
            return action;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserActionStats() {
        List<Object[]> userActionStats = logRepository.getUserActionStats();
        
        return userActionStats.stream().map(stat -> {
            Map<String, Object> userAction = new HashMap<>();
            userAction.put("userId", stat[0]);
            userAction.put("count", stat[1]);
            return userAction;
        }).collect(Collectors.toList());
    }
    
    @Override
    public void cleanExpiredLogs(LocalDateTime before) {
        // 这里可以实现清理过期日志的逻辑
        // 由于JPA没有直接的批量删除方法，可以使用原生SQL或分批删除
    }
    
    /**
     * 转换为DTO
     */
    private LogDto convertToDto(Log log) {
        return new LogDto(
                log.getId(),
                log.getUserId(),
                log.getAction(),
                log.getDescription(),
                log.getPath(),
                log.getMethod(),
                log.getIp(),
                log.getUserAgent(),
                log.getRequestParams(),
                log.getResponseStatus(),
                log.getExecutionTime(),
                log.getErrorMessage(),
                log.getCreatedAt()
        );
    }
} 