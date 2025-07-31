package com.bijiabao.controller;

import com.bijiabao.dto.ProductDto;
import com.bijiabao.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@Slf4j
@RestController
@RequestMapping("/favorites")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class FavoriteController {
    
    private final FavoriteService favoriteService = null;
    
    /**
     * 添加收藏
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addFavorite(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long productId = request.get("productId");
        
        log.info("添加收藏请求 - 用户ID: {}, 商品ID: {}", userId, productId);
        
        boolean success = favoriteService.addFavorite(userId, productId);
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "收藏成功"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "收藏失败"
            ));
        }
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> removeFavorite(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long productId = request.get("productId");
        
        log.info("取消收藏请求 - 用户ID: {}, 商品ID: {}", userId, productId);
        
        boolean success = favoriteService.removeFavorite(userId, productId);
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "取消收藏成功"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "取消收藏失败"
            ));
        }
    }
    
    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        
        log.info("检查收藏状态 - 用户ID: {}, 商品ID: {}", userId, productId);
        
        boolean isFavorited = favoriteService.isFavorited(userId, productId);
        return ResponseEntity.ok(Map.of("isFavorited", isFavorited));
    }
    
    /**
     * 获取用户收藏列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ProductDto>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("获取用户收藏列表 - 用户ID: {}, 页码: {}, 大小: {}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> favorites = favoriteService.getUserFavorites(userId, pageable);
        
        return ResponseEntity.ok(favorites);
    }
    
    /**
     * 获取商品收藏数量
     */
    @GetMapping("/count/product/{productId}")
    public ResponseEntity<Map<String, Long>> getProductFavoriteCount(@PathVariable Long productId) {
        log.info("获取商品收藏数量: {}", productId);
        
        long count = favoriteService.getFavoriteCount(productId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    /**
     * 获取用户收藏数量
     */
    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Map<String, Long>> getUserFavoriteCount(@PathVariable Long userId) {
        log.info("获取用户收藏数量: {}", userId);
        
        long count = favoriteService.getUserFavoriteCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    /**
     * 获取用户收藏的商品ID列表
     */
    @GetMapping("/product-ids/{userId}")
    public ResponseEntity<List<Long>> getUserFavoriteProductIds(@PathVariable Long userId) {
        log.info("获取用户收藏的商品ID列表: {}", userId);
        
        List<Long> productIds = favoriteService.getUserFavoriteProductIds(userId);
        return ResponseEntity.ok(productIds);
    }
    
    public void someMethod() {
        log.info("Executing someMethod in FavoriteController");
    }
}