package com.bijiabao.patterns.strategy;

import com.bijiabao.dto.PredictionResult;

import java.util.Map;

/**
 * 价格预测策略接口
 * 策略模式 - 定义预测算法的统一接口
 */
public interface PredictionStrategy {
    
    /**
     * 执行价格预测
     * 
     * @param productId 商品ID
     * @param options 预测选项
     * @return 预测结果
     */
    PredictionResult predict(Long productId, Map<String, Object> options);
    
    /**
     * 获取策略名称
     * 
     * @return 策略名称
     */
    String getStrategyName();
    
    /**
     * 获取策略描述
     * 
     * @return 策略描述
     */
    String getDescription();
    
    /**
     * 检查策略是否适用于给定的数据特征
     * 
     * @param dataFeatures 数据特征
     * @return 是否适用
     */
    boolean isApplicable(Map<String, Object> dataFeatures);
} 