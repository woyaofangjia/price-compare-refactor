package com.bijiabao.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 日志实体类
 */
@Entity
@Table(name = "logs")
@EntityListeners(AuditingEntityListener.class)
public class Log {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;
    
    /**
     * 操作类型
     */
    @Column(nullable = false, length = 50)
    private String action;
    
    /**
     * 操作描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * 请求路径
     */
    @Column(length = 500)
    private String path;
    
    /**
     * 请求方法
     */
    @Column(length = 10)
    private String method;
    
    /**
     * IP地址
     */
    @Column(length = 50)
    private String ip;
    
    /**
     * 用户代理
     */
    @Column(length = 500)
    private String userAgent;
    
    /**
     * 请求参数
     */
    @Column(columnDefinition = "TEXT")
    private String requestParams;
    
    /**
     * 响应状态
     */
    @Column(name = "response_status")
    private Integer responseStatus;
    
    /**
     * 执行时间（毫秒）
     */
    @Column(name = "execution_time")
    private Long executionTime;
    
    /**
     * 错误信息
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getRequestParams() {
        return requestParams;
    }
    
    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }
    
    public Integer getResponseStatus() {
        return responseStatus;
    }
    
    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }
    
    public Long getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}