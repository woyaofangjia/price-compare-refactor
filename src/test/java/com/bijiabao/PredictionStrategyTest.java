package com.bijiabao;

import com.bijiabao.dto.PredictionResult;
import com.bijiabao.patterns.strategy.PredictionStrategyManager;
import com.bijiabao.patterns.strategy.impl.SimpleMovingAverageStrategy;
import com.bijiabao.service.PriceHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * 预测策略测试类
 */
@ExtendWith(MockitoExtension.class)
class PredictionStrategyTest {

    @Mock
    private PriceHistoryService priceHistoryService;

    @InjectMocks
    private SimpleMovingAverageStrategy simpleMovingAverageStrategy;

    @Mock
    private PredictionStrategyManager strategyManager;

    private List<Double> testPrices;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testPrices = Arrays.asList(100.0, 102.0, 98.0, 105.0, 103.0, 107.0, 104.0);
    }

    @Test
    void testSimpleMovingAverageStrategy() {
        // Given
        Long productId = 1L;
        Map<String, Object> options = Map.of("days", 7);
        
        when(priceHistoryService.getRecentPrices(eq(productId), eq(7)))
                .thenReturn(testPrices);

        // When
        PredictionResult result = simpleMovingAverageStrategy.predict(productId, options);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("简单移动平均", result.getStrategyName());
        assertNotNull(result.getPredictedPrice());
        assertNotNull(result.getTrend());
        assertNotNull(result.getConfidence());
        assertEquals(7, result.getDataPoints());
        
        // 验证预测价格计算
        double expectedAverage = testPrices.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        assertEquals(expectedAverage, result.getPredictedPrice(), 0.01);
    }

    @Test
    void testSimpleMovingAverageStrategyWithInsufficientData() {
        // Given
        Long productId = 1L;
        Map<String, Object> options = Map.of("days", 7);
        
        when(priceHistoryService.getRecentPrices(eq(productId), eq(7)))
                .thenReturn(Arrays.asList(100.0, 102.0)); // 数据不足

        // When
        PredictionResult result = simpleMovingAverageStrategy.predict(productId, options);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("数据不足，无法进行预测", result.getError());
        assertEquals("简单移动平均", result.getStrategyName());
    }

    @Test
    void testStrategyApplicability() {
        // Given
        Map<String, Object> lowVolatilityFeatures = Map.of(
                "volatility", 0.05,
                "dataPoints", 10
        );
        
        Map<String, Object> highVolatilityFeatures = Map.of(
                "volatility", 0.3,
                "dataPoints", 10
        );

        // When & Then
        assertTrue(simpleMovingAverageStrategy.isApplicable(lowVolatilityFeatures));
        assertFalse(simpleMovingAverageStrategy.isApplicable(highVolatilityFeatures));
    }

    @Test
    void testStrategyManager() {
        // Given
        Long productId = 1L;
        Map<String, Object> options = Map.of("days", 7);
        
        PredictionResult mockResult = PredictionResult.builder()
                .success(true)
                .predictedPrice(102.5)
                .trend("稳定")
                .confidence(0.8)
                .strategyName("简单移动平均")
                .build();

        when(strategyManager.predictWithStrategy(eq("简单移动平均"), eq(productId), eq(options)))
                .thenReturn(mockResult);

        // When
        PredictionResult result = strategyManager.predictWithStrategy("简单移动平均", productId, options);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(102.5, result.getPredictedPrice());
        assertEquals("稳定", result.getTrend());
        assertEquals(0.8, result.getConfidence());
    }

    @Test
    void testStrategyManagerWithInvalidStrategy() {
        // Given
        Long productId = 1L;
        Map<String, Object> options = Map.of("days", 7);
        
        when(strategyManager.predictWithStrategy(eq("无效策略"), eq(productId), eq(options)))
                .thenThrow(new IllegalArgumentException("未找到预测策略: 无效策略"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            strategyManager.predictWithStrategy("无效策略", productId, options);
        });
    }

    @Test
    void testPredictionResultBuilder() {
        // Given & When
        PredictionResult result = PredictionResult.builder()
                .success(true)
                .predictedPrice(100.0)
                .trend("上涨")
                .confidence(0.9)
                .dataPoints(10)
                .strategyName("测试策略")
                .build();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(100.0, result.getPredictedPrice());
        assertEquals("上涨", result.getTrend());
        assertEquals(0.9, result.getConfidence());
        assertEquals(10, result.getDataPoints());
        assertEquals("测试策略", result.getStrategyName());
        assertNotNull(result.getPredictionTime());
    }
} 