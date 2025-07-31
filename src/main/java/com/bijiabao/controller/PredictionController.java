package com.bijiabao.controller;

import com.bijiabao.dto.PredictionResult;
import com.bijiabao.patterns.strategy.PredictionStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 价格预测控制器
 */
@Slf4j
@RestController
@RequestMapping("/prediction")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class PredictionController {
    
    private final PredictionStrategyManager strategyManager;
    
    /**
     * 获取可用预测策略
     */
    @GetMapping("/strategies")
    public ResponseEntity<List<PredictionStrategyManager.StrategyInfo>> getAvailableStrategies() {
        log.info("获取可用预测策略");
        List<PredictionStrategyManager.StrategyInfo> strategies = strategyManager.getAvailableStrategies();
        return ResponseEntity.ok(strategies);
    }
    
    /**
     * 使用指定策略进行预测
     */
    @GetMapping("/{productId}")
    public ResponseEntity<PredictionResult> predict(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "auto") String strategy,
            @RequestParam Map<String, Object> options) {
        
        log.info("价格预测请求 - 商品ID: {}, 策略: {}", productId, strategy);
        
        try {
            PredictionResult result;
            
            if ("auto".equals(strategy)) {
                // 自动选择策略
                Map<String, Object> dataFeatures = getDataFeatures(productId);
                String recommendedStrategy = strategyManager.getRecommendedStrategy(productId, dataFeatures);
                result = strategyManager.predictWithStrategy(recommendedStrategy, productId, options);
            } else if ("all".equals(strategy)) {
                // 使用所有策略并返回最佳结果
                Map<String, PredictionResult> allResults = strategyManager.predictWithAllStrategies(productId, options);
                result = selectBestResult(allResults);
            } else {
                // 使用指定策略
                result = strategyManager.predictWithStrategy(strategy, productId, options);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("价格预测失败", e);
            PredictionResult errorResult = PredictionResult.builder()
                    .success(false)
                    .error("预测失败: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
    
    /**
     * 使用所有策略进行预测
     */
    @GetMapping("/{productId}/all")
    public ResponseEntity<Map<String, PredictionResult>> predictWithAllStrategies(
            @PathVariable Long productId,
            @RequestParam Map<String, Object> options) {
        
        log.info("使用所有策略进行预测 - 商品ID: {}", productId);
        
        try {
            Map<String, PredictionResult> results = strategyManager.predictWithAllStrategies(productId, options);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("多策略预测失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取数据特征（用于策略选择）
     */
    private Map<String, Object> getDataFeatures(Long productId) {
        // 这里应该调用服务获取数据特征
        // 暂时返回空Map
        return Map.of();
    }
    
    /**
     * 选择最佳预测结果
     */
    private PredictionResult selectBestResult(Map<String, PredictionResult> results) {
        return results.values().stream()
                .filter(PredictionResult::isSuccess)
                .max((r1, r2) -> Double.compare(
                        r1.getConfidence() != null ? r1.getConfidence() : 0.0,
                        r2.getConfidence() != null ? r2.getConfidence() : 0.0
                ))
                .orElse(PredictionResult.builder()
                        .success(false)
                        .error("所有策略预测失败")
                        .build());
    }
} 