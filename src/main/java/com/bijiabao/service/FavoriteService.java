package com.bijiabao.service;

import com.bijiabao.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 收藏服务接口
 */
public interface FavoriteService {
    
    /**
     * 添加收藏
     */
    boolean addFavorite(Long userId, Long productId);
    
    /**
     * 取消收藏
     */
    boolean removeFavorite(Long userId, Long productId);
    
    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long productId);
    
    /**
     * 获取用户收藏列表
     */
    Page<ProductDto> getUserFavorites(Long userId, Pageable pageable);
    
    /**
     * 获取商品收藏数量
     */
    long getFavoriteCount(Long productId);
    
    /**
     * 获取用户收藏数量
     */
    long getUserFavoriteCount(Long userId);
    
    /**
     * 获取用户收藏的商品ID列表
     */
    List<Long> getUserFavoriteProductIds(Long userId);
} 