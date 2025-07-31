package com.bijiabao.controller;

import com.bijiabao.dto.ProductDto;
import com.bijiabao.service.RecommendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 推荐控制器
 */
@RestController
@RequestMapping("/recommend")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class RecommendController {
    
    private final RecommendService recommendService;
    
    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }
    
    /**
     * 获取热门推荐
     */
    @GetMapping("/hot")
    public ResponseEntity<List<ProductDto>> getHotRecommendations(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductDto> recommendations = recommendService.getHotRecommendations(limit);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * 获取基于用户行为的推荐
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductDto>> getUserBasedRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductDto> recommendations = recommendService.getUserBasedRecommendations(userId, limit);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * 获取基于商品相似度的推荐
     */
    @GetMapping("/item/{productId}")
    public ResponseEntity<List<ProductDto>> getItemBasedRecommendations(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductDto> recommendations = recommendService.getItemBasedRecommendations(productId, limit);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * 获取基于分类的推荐
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDto>> getCategoryBasedRecommendations(
            @PathVariable String category,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductDto> recommendations = recommendService.getCategoryBasedRecommendations(category, limit);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * 获取基于品牌的推荐
     */
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<ProductDto>> getBrandBasedRecommendations(
            @PathVariable Long brandId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductDto> recommendations = recommendService.getBrandBasedRecommendations(brandId, limit);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * 获取降价推荐
     */
    @GetMapping("/price-drop")
    public ResponseEntity<List<ProductDto>> getPriceDropRecommendations(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductDto> recommendations = recommendService.getPriceDropRecommendations(limit);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * 获取个性化推荐
     */
    @GetMapping("/personalized/{userId}")
    public ResponseEntity<List<ProductDto>> getPersonalizedRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ProductDto> recommendations = recommendService.getPersonalizedRecommendations(userId, limit);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * 获取推荐统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRecommendationStats() {
        Map<String, Object> stats = recommendService.getRecommendationStats();
        return ResponseEntity.ok(stats);
    }
} 