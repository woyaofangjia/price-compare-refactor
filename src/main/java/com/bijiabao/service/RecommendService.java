package com.bijiabao.service;

import com.bijiabao.dto.ProductDto;

import java.util.List;
import java.util.Map;

/**
 * 推荐服务接口
 */
public interface RecommendService {
    
    /**
     * 获取热门推荐商品
     */
    List<ProductDto> getHotRecommendations(int limit);
    
    /**
     * 获取基于用户行为的推荐商品
     */
    List<ProductDto> getUserBasedRecommendations(Long userId, int limit);
    
    /**
     * 获取基于商品相似度的推荐商品
     */
    List<ProductDto> getItemBasedRecommendations(Long productId, int limit);
    
    /**
     * 获取基于分类的推荐商品
     */
    List<ProductDto> getCategoryBasedRecommendations(String category, int limit);
    
    /**
     * 获取基于品牌的推荐商品
     */
    List<ProductDto> getBrandBasedRecommendations(Long brandId, int limit);
    
    /**
     * 获取降价推荐商品
     */
    List<ProductDto> getPriceDropRecommendations(int limit);
    
    /**
     * 获取个性化推荐商品
     */
    List<ProductDto> getPersonalizedRecommendations(Long userId, int limit);
    
    /**
     * 获取推荐统计信息
     */
    Map<String, Object> getRecommendationStats();
} 