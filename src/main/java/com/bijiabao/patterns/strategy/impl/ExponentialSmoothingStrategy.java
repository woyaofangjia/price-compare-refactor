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
 * 指数平滑策略实现
 * 策略模式 - 具体策略实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExponentialSmoothingStrategy implements PredictionStrategy {
    
    private final PriceHistoryService priceHistoryService;
    
    @Override
    public PredictionResult predict(Long productId, Map<String, Object> options) {
        log.info("执行指数平滑预测 - 商品ID: {}", productId);
        
        try {
            // 获取参数
            double alpha = (double) options.getOrDefault("alpha", 0.3);
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
            
            // 计算指数平滑
            double smoothedPrice = exponentialSmoothing(prices, alpha);
            
            // 计算趋势
            String trend = calculateTrend(prices);
            
            // 计算置信度
            double confidence = calculateConfidence(prices, alpha);
            
            return PredictionResult.builder()
                    .success(true)
                    .predictedPrice(smoothedPrice)
                    .trend(trend)
                    .confidence(confidence)
                    .dataPoints(prices.size())
                    .strategyName(getStrategyName())
                    .build();
                    
        } catch (Exception e) {
            log.error("指数平滑预测失败", e);
            return PredictionResult.builder()
                    .success(false)
                    .error("预测失败: " + e.getMessage())
                    .strategyName(getStrategyName())
                    .build();
        }
    }
    
    @Override
    public String getStrategyName() {
        return "指数平滑";
    }
    
    @Override
    public String getDescription() {
        return "指数平滑，适用于有趋势性的价格数据";
    }
    
    @Override
    public boolean isApplicable(Map<String, Object> dataFeatures) {
        // 检查数据特征是否适合指数平滑
        Double volatility = (Double) dataFeatures.get("volatility");
        Integer dataPoints = (Integer) dataFeatures.get("dataPoints");
        
        return volatility != null && volatility >= 0.1 && volatility < 0.2 && 
               dataPoints != null && dataPoints >= 3;
    }
    
    /**
     * 指数平滑计算
     */
    private double exponentialSmoothing(List<Double> prices, double alpha) {
        if (prices.isEmpty()) {
            return 0.0;
        }
        
        double smoothed = prices.get(0); // 初始值设为第一个价格
        
        for (int i = 1; i < prices.size(); i++) {
            smoothed = alpha * prices.get(i) + (1 - alpha) * smoothed;
        }
        
        return smoothed;
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
    private double calculateConfidence(List<Double> prices, double alpha) {
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
        
        double baseConfidence = 0.5;
        if (coefficientOfVariation < 0.1) baseConfidence = 0.9;
        else if (coefficientOfVariation < 0.2) baseConfidence = 0.7;
        else if (coefficientOfVariation < 0.3) baseConfidence = 0.5;
        else baseConfidence = 0.3;
        
        // alpha值越接近0.5，置信度越高
        double alphaFactor = 1 - Math.abs(alpha - 0.5) * 2;
        
        return Math.max(0.1, baseConfidence * alphaFactor);
    }
} 