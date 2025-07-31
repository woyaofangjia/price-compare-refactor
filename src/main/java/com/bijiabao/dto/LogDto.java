package com.bijiabao.dto;

import java.time.LocalDateTime;

/**
 * 日志数据传输对象
 */
public class LogDto {
    
    private Integer id;
    private Long userId;
    private String action;
    private String description;
    private String path;
    private String method;
    private String ip;
    private String userAgent;
    private String requestParams;
    private Integer responseStatus;
    private Long executionTime;
    private String errorMessage;
    private LocalDateTime createdAt;
    
    // 扩展字段
    private String username;
    
    // Constructors
    public LogDto() {}
    
    public LogDto(Integer id, Long userId, String action, String description, String path, 
                  String method, String ip, String userAgent, String requestParams, 
                  Integer responseStatus, Long executionTime, String errorMessage, 
                  LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.description = description;
        this.path = path;
        this.method = method;
        this.ip = ip;
        this.userAgent = userAgent;
        this.requestParams = requestParams;
        this.responseStatus = responseStatus;
        this.executionTime = executionTime;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }
    
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}