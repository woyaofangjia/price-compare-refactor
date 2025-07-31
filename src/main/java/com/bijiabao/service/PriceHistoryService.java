package com.bijiabao.service;

import java.util.List;

/**
 * 价格历史服务接口
 */
public interface PriceHistoryService {
    
    /**
     * 获取商品最近的价格数据
     * 
     * @param productId 商品ID
     * @param days 天数
     * @return 价格列表
     */
    List<Double> getRecentPrices(Long productId, int days);
    
    /**
     * 获取商品价格历史
     * 
     * @param productId 商品ID
     * @return 价格历史列表
     */
    List<Double> getPriceHistory(Long productId);
    
    /**
     * 添加价格记录
     * 
     * @param productId 商品ID
     * @param platform 平台
     * @param price 价格
     */
    void addPriceRecord(Long productId, String platform, Double price);
    
    /**
     * 获取商品价格波动性
     * 
     * @param productId 商品ID
     * @return 波动性系数
     */
    Double getPriceVolatility(Long productId);
    
    /**
     * 获取商品数据特征
     * 
     * @param productId 商品ID
     * @return 数据特征
     */
    java.util.Map<String, Object> getDataFeatures(Long productId);
} 