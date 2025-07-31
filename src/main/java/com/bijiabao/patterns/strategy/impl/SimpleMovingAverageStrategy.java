package com.bijiabao.patterns.strategy.impl;

import com.bijiabao.dto.PredictionResult;
import com.bijiabao.patterns.strategy.PredictionStrategy;
import com.bijiabao.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 简单移动平均策略实现
 * 策略模式 - 具体策略实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleMovingAverageStrategy implements PredictionStrategy {
    
    private final PriceHistoryService priceHistoryService;
    
    @Override
    public PredictionResult predict(Long productId, Map<String, Object> options) {
        log.info("执行简单移动平均预测 - 商品ID: {}", productId);
        
        try {
            // 获取参数
            int days = (int) options.getOrDefault("days", 7);
            
            // 获取价格历史数据
            List<Double> prices = priceHistoryService.getRecentPrices(productId, days);
            
            if (prices.size() < 3) {
                return PredictionResult.builder()
                        .success(false)
                        .error("数据不足，无法进行预测")
                        .strategyName(getStrategyName())
                        .build();
            }
            
            // 计算简单移动平均
            double average = prices.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            // 计算趋势
            String trend = calculateTrend(prices);
            
            // 计算置信度
            double confidence = calculateConfidence(prices);
            
            return PredictionResult.builder()
                    .success(true)
                    .predictedPrice(average)
                    .trend(trend)
                    .confidence(confidence)
                    .dataPoints(prices.size())
                    .strategyName(getStrategyName())
                    .build();
                    
        } catch (Exception e) {
            log.error("简单移动平均预测失败", e);
            return PredictionResult.builder()
                    .success(false)
                    .error("预测失败: " + e.getMessage())
                    .strategyName(getStrategyName())
                    .build();
        }
    }
    
    @Override
    public String getStrategyName() {
        return "简单移动平均";
    }
    
    @Override
    public String getDescription() {
        return "简单移动平均，适用于价格波动较小的商品";
    }
    
    @Override
    public boolean isApplicable(Map<String, Object> dataFeatures) {
        // 检查数据特征是否适合简单移动平均
        Double volatility = (Double) dataFeatures.get("volatility");
        Integer dataPoints = (Integer) dataFeatures.get("dataPoints");
        
        return volatility != null && volatility < 0.1 && 
               dataPoints != null && dataPoints >= 3;
    }
    
    /**
     * 计算价格趋势
     */
    private String calculateTrend(List<Double> prices) {
        if (prices.size() < 2) {
            return "稳定";
        }
        
        int mid = prices.size() / 2;
        List<Double> firstHalf = prices.subList(0, mid);
        List<Double> secondHalf = prices.subList(mid, prices.size());
        
        double firstAvg = firstHalf.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double secondAvg = secondHalf.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        double change = ((secondAvg - firstAvg) / firstAvg) * 100;
        
        if (change > 5) return "上涨";
        if (change < -5) return "下跌";
        return "稳定";
    }
    
    /**
     * 计算预测置信度
     */
    private double calculateConfidence(List<Double> prices) {
        if (prices.size() < 3) {
            return 0.3;
        }
        
        double mean = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = prices.stream()
                .mapToDouble(price -> Math.pow(price - mean, 2))
                .average()
                .orElse(0.0);
        double stdDev = Math.sqrt(variance);
        
        // 基于变异系数的置信度计算
        double coefficientOfVariation = stdDev / mean;
        
        if (coefficientOfVariation < 0.1) return 0.9;
        if (coefficientOfVariation < 0.2) return 0.7;
        if (coefficientOfVariation < 0.3) return 0.5;
        return 0.3;
    }
} 