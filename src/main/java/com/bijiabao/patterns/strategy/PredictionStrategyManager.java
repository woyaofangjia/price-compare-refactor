package com.bijiabao.patterns.strategy;

import com.bijiabao.dto.PredictionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 预测策略管理器
 * 策略模式 - 策略管理和选择
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PredictionStrategyManager {
    
    private final List<PredictionStrategy> strategies;
    private final Map<String, PredictionStrategy> strategyMap = new ConcurrentHashMap<>();
    
    /**
     * 初始化策略映射
     */
    public void initializeStrategies() {
        strategies.forEach(strategy -> {
            strategyMap.put(strategy.getStrategyName(), strategy);
            log.info("注册预测策略: {} - {}", strategy.getStrategyName(), strategy.getDescription());
        });
    }
    
    /**
     * 使用指定策略进行预测
     */
    public PredictionResult predictWithStrategy(String strategyName, Long productId, Map<String, Object> options) {
        PredictionStrategy strategy = strategyMap.get(strategyName);
        if (strategy == null) {
            throw new IllegalArgumentException("未找到预测策略: " + strategyName);
        }
        
        log.info("使用策略 {} 进行预测 - 商品ID: {}", strategyName, productId);
        return strategy.predict(productId, options);
    }
    
    /**
     * 使用所有策略进行预测并比较结果
     */
    public Map<String, PredictionResult> predictWithAllStrategies(Long productId, Map<String, Object> options) {
        Map<String, PredictionResult> results = new ConcurrentHashMap<>();
        
        strategies.parallelStream().forEach(strategy -> {
            try {
                PredictionResult result = strategy.predict(productId, options);
                results.put(strategy.getStrategyName(), result);
            } catch (Exception e) {
                log.error("策略 {} 预测失败", strategy.getStrategyName(), e);
                results.put(strategy.getStrategyName(), 
                    PredictionResult.builder()
                        .success(false)
                        .error("预测失败: " + e.getMessage())
                        .strategyName(strategy.getStrategyName())
                        .build());
            }
        });
        
        return results;
    }
    
    /**
     * 获取推荐策略
     */
    public String getRecommendedStrategy(Long productId, Map<String, Object> dataFeatures) {
        if (dataFeatures == null || dataFeatures.isEmpty()) {
            return "简单移动平均"; // 默认策略
        }
        
        // 根据数据特征选择最适合的策略
        for (PredictionStrategy strategy : strategies) {
            if (strategy.isApplicable(dataFeatures)) {
                log.info("推荐策略: {} - 商品ID: {}", strategy.getStrategyName(), productId);
                return strategy.getStrategyName();
            }
        }
        
        // 如果没有找到合适的策略，返回默认策略
        return "简单移动平均";
    }
    
    /**
     * 获取所有可用策略信息
     */
    public List<StrategyInfo> getAvailableStrategies() {
        return strategies.stream()
                .map(strategy -> StrategyInfo.builder()
                        .name(strategy.getStrategyName())
                        .description(strategy.getDescription())
                        .build())
                .toList();
    }
    
    /**
     * 策略信息DTO
     */
    public static class StrategyInfo {
        private String name;
        private String description;
        
        // Builder pattern
        public static StrategyInfoBuilder builder() {
            return new StrategyInfoBuilder();
        }
        
        public static class StrategyInfoBuilder {
            private StrategyInfo strategyInfo = new StrategyInfo();
            
            public StrategyInfoBuilder name(String name) {
                strategyInfo.name = name;
                return this;
            }
            
            public StrategyInfoBuilder description(String description) {
                strategyInfo.description = description;
                return this;
            }
            
            public StrategyInfo build() {
                return strategyInfo;
            }
        }
        
        // Getters
        public String getName() { return name; }
        public String getDescription() { return description; }
    }
} 