package com.bijiabao.service.impl;

import com.bijiabao.entity.ProductPrice;
import com.bijiabao.repository.ProductPriceRepository;
import com.bijiabao.service.PriceHistoryService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * 价格历史服务实现类
 */
@Slf4j
@Service
public class PriceHistoryServiceImpl implements PriceHistoryService {
    
    private final ProductPriceRepository productPriceRepository = null;

    @Override
    public List<Double> getRecentPrices(Long productId, int days) {
        log.debug("获取商品 {} 最近 {} 天的价格数据", productId, days);
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        List<ProductPrice> prices = productPriceRepository
                .findByProductIdAndPriceDateAfterOrderByPriceDateDesc(productId, startDate);
        
        return prices.stream()
                .map(price -> price.getPrice().doubleValue())
                .collect(Collectors.toList());
    }
    
    @Override
    @Cacheable(value = "priceHistory", key = "#productId")
    public List<Double> getPriceHistory(Long productId) {
        log.debug("获取商品 {} 的价格历史", productId);
        
        List<ProductPrice> prices = productPriceRepository
                .findByProductIdOrderByPriceDateDesc(productId);
        
        return prices.stream()
                .map(price -> price.getPrice().doubleValue())
                .collect(Collectors.toList());
    }
    
    @Override
    public void addPriceRecord(Long productId, String platform, Double price) {
        log.info("添加价格记录 - 商品ID: {}, 平台: {}, 价格: {}", productId, platform, price);
        
        ProductPrice productPrice = new ProductPrice();
        productPrice.setProductId(productId);
        productPrice.setPlatform(platform);
        productPrice.setPrice(BigDecimal.valueOf(price));
        productPrice.setPriceDate(LocalDateTime.now());
        
        productPriceRepository.save(productPrice);
    }
    
    @Override
    @Cacheable(value = "priceVolatility", key = "#productId")
    public Double getPriceVolatility(Long productId) {
        log.debug("计算商品 {} 的价格波动性", productId);
        
        List<Double> prices = getPriceHistory(productId);
        
        if (prices.size() < 2) {
            return 0.0;
        }
        
        double mean = prices.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        
        double variance = prices.stream()
                .mapToDouble(price -> Math.pow(price - mean, 2))
                .average()
                .orElse(0.0);
        
        double stdDev = Math.sqrt(variance);
        
        return stdDev / mean; // 变异系数
    }
    
    @Override
    @Cacheable(value = "dataFeatures", key = "#productId")
    public Map<String, Object> getDataFeatures(Long productId) {
        log.debug("获取商品 {} 的数据特征", productId);
        
        List<Double> prices = getPriceHistory(productId);
        
        Map<String, Object> features = new java.util.HashMap<>();
        features.put("dataPoints", prices.size());
        features.put("volatility", getPriceVolatility(productId));
        
        if (prices.size() >= 2) {
            // 计算趋势
            double firstHalf = prices.subList(0, prices.size() / 2).stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            double secondHalf = prices.subList(prices.size() / 2, prices.size()).stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            double trend = ((secondHalf - firstHalf) / firstHalf) * 100;
            features.put("trend", trend);
        }
        
        return features;
    }
    
    public void someMethod() {
        log.info("Executing someMethod in PriceHistoryServiceImpl");
    }
}