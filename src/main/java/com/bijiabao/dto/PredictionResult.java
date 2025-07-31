package com.bijiabao.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预测结果数据传输对象
 */
@Data
@Builder
public class PredictionResult {
    
    /**
     * 预测是否成功
     */
    private boolean success;
    
    /**
     * 预测价格
     */
    private Double predictedPrice;
    
    /**
     * 价格趋势
     */
    private String trend;
    
    /**
     * 预测置信度 (0-1)
     */
    private Double confidence;
    
    /**
     * 数据点数量
     */
    private Integer dataPoints;
    
    /**
     * 策略名称
     */
    private String strategyName;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 预测时间
     */
    @Builder.Default
    private LocalDateTime predictionTime = LocalDateTime.now();
    
    /**
     * 额外参数
     */
    private Object additionalParams;
} 